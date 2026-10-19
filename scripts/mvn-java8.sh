#!/usr/bin/env bash
# 使用 JDK 1.8 运行 Maven（本项目 pom 目标 java.version=1.8）
set -euo pipefail

if [[ "$(uname -s)" == "Darwin" ]]; then
  export JAVA_HOME="$(/usr/libexec/java_home -v 1.8 2>/dev/null || true)"
fi

if [[ -z "${JAVA_HOME:-}" || ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "未找到 JDK 1.8。macOS 可安装后执行: /usr/libexec/java_home -V" >&2
  echo "或手动设置: export JAVA_HOME=/path/to/jdk1.8" >&2
  exit 1
fi

export PATH="$JAVA_HOME/bin:$PATH"
echo "Using Java: $($JAVA_HOME/bin/java -version 2>&1 | head -1)" >&2
exec mvn "$@"
