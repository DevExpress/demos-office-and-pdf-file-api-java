#!/usr/bin/env python3
"""
Launch every installed Java console demo and treat a non-zero exit code as a failure.
Usage:
    run-java-demos.py <demos-dir> [--timeout SECONDS] [--report PATH]
"""
from __future__ import annotations

import argparse
import os
import shutil
import subprocess
import sys
import tempfile
import time
import zipfile
from pathlib import Path
from xml.sax.saxutils import escape, quoteattr


def java_command() -> str:
    """Absolute path to the java launcher (honours JAVA_HOME), else 'java' from PATH."""
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        exe = "java.exe" if os.name == "nt" else "java"
        candidate = Path(java_home) / "bin" / exe
        if candidate.is_file():
            return str(candidate)
    return "java"


def read_main_class(jar: Path):
    """Return the Main-Class from a jar's manifest, or None (unfolds RFC822 continuation lines)."""
    try:
        with zipfile.ZipFile(jar) as zf:
            raw = zf.read("META-INF/MANIFEST.MF").decode("utf-8", "replace")
    except (KeyError, zipfile.BadZipFile, OSError):
        return None
    lines: list[str] = []
    for line in raw.replace("\r\n", "\n").replace("\r", "\n").split("\n"):
        if line.startswith(" ") and lines:  # continuation of the previous header
            lines[-1] += line[1:]
        else:
            lines.append(line)
    for line in lines:
        if line.lower().startswith("main-class:"):
            return line.split(":", 1)[1].strip() or None
    return None


def discover(demos_dir: Path):
    """Return sorted (classname, name, lib_dir, main_class) for every runnable installed demo."""
    demos = []
    for lib_dir in sorted(demos_dir.glob("**/build/install/*/lib")):
        dist_name = lib_dir.parent.name  # .../install/<dist_name>/lib
        app_jar = lib_dir / f"{dist_name}.jar"
        main_class = read_main_class(app_jar) if app_jar.is_file() else None
        if main_class is None:  # fall back to any jar in lib/ that declares a Main-Class
            for jar in sorted(lib_dir.glob("*.jar")):
                main_class = read_main_class(jar)
                if main_class:
                    break
        if not main_class:
            # Intermediate grouping project (no application entry point) - nothing to run.
            continue
        # lib -> <dist> -> install -> build -> <project dir>
        proj_dir = lib_dir.parents[3]
        rel = proj_dir.relative_to(demos_dir)
        parent = str(rel.parent)
        classname = parent.replace(os.sep, ".") if parent not in (".", "") else dist_name
        demos.append((classname, dist_name, lib_dir, main_class))
    return demos


def run_one(java: str, lib_dir: Path, main_class: str, timeout: int):
    """Run a single demo; return (exit_code, combined_output, duration_s, timed_out)."""
    workdir = tempfile.mkdtemp()
    cmd = [java, "-cp", os.path.join(str(lib_dir), "*"), main_class]
    start = time.monotonic()
    try:
        proc = subprocess.run(
            cmd,
            cwd=workdir,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=timeout,
        )
        code = proc.returncode
        output = proc.stdout.decode("utf-8", "replace")
        timed_out = False
    except subprocess.TimeoutExpired as exc:
        code = 124
        captured = exc.stdout.decode("utf-8", "replace") if exc.stdout else ""
        output = f"{captured}\n[killed after {timeout}s timeout]"
        timed_out = True
    finally:
        shutil.rmtree(workdir, ignore_errors=True)
    return code, output, time.monotonic() - start, timed_out


def write_report(path: Path, results, suite_time: float) -> None:
    total = len(results)
    failures = sum(1 for r in results if r["code"] != 0)
    lines = ['<?xml version="1.0" encoding="UTF-8"?>']
    lines.append(
        f'<testsuite name="Java Demos" tests="{total}" failures="{failures}" '
        f'errors="0" skipped="0" time="{suite_time:.0f}">'
    )
    for r in results:
        attrs = (
            f"name={quoteattr(r['name'])} "
            f"classname={quoteattr(r['classname'])} "
            f'time="{r["time"]:.0f}"'
        )
        if r["code"] == 0:
            lines.append(f"  <testcase {attrs}/>")
        else:
            lines.append(f"  <testcase {attrs}>")
            lines.append(f'    <failure message="Demo exited with code {r["code"]}">')
            lines.append(escape(r["output"]))
            lines.append("    </failure>")
            lines.append("  </testcase>")
    lines.append("</testsuite>")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("demos_dir", type=Path, help="Console demos root (contains settings.gradle).")
    parser.add_argument("--timeout", type=int, default=180, help="Hard timeout per demo, seconds.")
    parser.add_argument("--report", type=Path, default=None, help="JUnit XML output path.")
    args = parser.parse_args()

    demos_dir = args.demos_dir.resolve()
    report = args.report or (demos_dir / "build" / "demo-test-results" / "TEST-java-demos.xml")

    demos = discover(demos_dir)
    if not demos:
        print(
            "::error title=No demos found::No runnable demo distributions were produced under build/install",
            file=sys.stderr,
        )
        return 1

    java = java_command()
    print(f"Discovered {len(demos)} runnable demo application(s) under {demos_dir}; java: {java}")
    results = []
    suite_start = time.monotonic()
    for classname, name, lib_dir, main_class in demos:
        code, output, duration, timed_out = run_one(java, lib_dir, main_class, args.timeout)
        if code == 0:
            print(f"PASS ({duration:.0f}s): {classname}.{name}")
        else:
            note = " (timeout)" if timed_out else ""
            print(f"::error title=Demo failed::{classname}.{name} exited with code {code}{note}")
            print(f"----- output of {classname}.{name} (exit {code}) -----")
            print(output)
            print("----------------------------------------------------------")
        results.append(
            {"classname": classname, "name": name, "code": code, "output": output, "time": duration}
        )
    suite_time = time.monotonic() - suite_start

    write_report(report, results, suite_time)

    failures = sum(1 for r in results if r["code"] != 0)
    print(f"Ran {len(results)} demo application(s); {failures} failed. Report: {report}")
    if failures:
        print(
            f"::error title=Java Demos Failed::{failures} of {len(results)} "
            "demo application(s) returned a non-zero exit code"
        )
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
