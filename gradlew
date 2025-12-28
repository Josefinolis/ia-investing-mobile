#!/bin/sh

#
# Gradle start up script for POSIX
#

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
app_path=$0

# Need this for daisy-chained symlinks.
while [ -h "$app_path" ] ; do
    ls=$( ls -ld "$app_path" )
    link=$( expr "$ls" : '.*-> \(.*\)$' )
    if expr "$link" : '/.*' > /dev/null; then
        app_path=$link
    else
        app_path=$( dirname "$app_path" )/$link
    fi
done

APP_HOME=$( cd -P "$( dirname "$app_path" )" > /dev/null 2>&1 && pwd )
APP_NAME="Gradle"
APP_BASE_NAME=$( basename "$0" )

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD=maximum

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

# OS specific support
cygwin=false
msys=false
darwin=false
nonstop=false
case "$( uname )" in
    CYGWIN* )         cygwin=true ;;
    Darwin* )         darwin=true ;;
    MSYS* | MINGW* )  msys=true ;;
    NONSTOP* )        nonstop=true ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD=java
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found"
fi

# Increase the maximum file descriptors
if ! "$cygwin" && ! "$darwin" && ! "$nonstop" ; then
    case $MAX_FD in
        max*)
            MAX_FD=$( ulimit -H -n ) ||
                warn "Could not query maximum file descriptor limit"
        esac
        case $MAX_FD in
            '' | soft) :;;
            *)
                ulimit -n "$MAX_FD" ||
                    warn "Could not set maximum file descriptor limit to $MAX_FD"
        esac
fi

# Collect all arguments for the java command, stacking in reverse order
#   * DEFAULT_JVM_OPTS, JAVA_OPTS, and GRADLE_OPTS environment variables
#   * $APP_HOME/gradle.properties (if it exists) for org.gradle.jvmargs
#   * Command-line arguments

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

set -- \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"

# Stop when "xargs" is not available.
if ! command -v xargs >/dev/null 2>&1
then
    die "xargs is not available"
fi

exec "$JAVACMD" "$@"
