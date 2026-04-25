#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPts='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

if [ $cygwin = "true" ] ; then
    # Increase the maximum file descriptors if we can.
    if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
        MAX_FD=`ulimit -H -n`
    fi
    if [ "$?" -eq 0 ] ; then
        ulimit -n $MAX_FD
    fi
    if [ "$?" -ne 0 ] ; then
        warn "Could not set maximum file descriptor limit: $MAX_FD"
    fi
    # Sometimes the path is not properly converted.
    # The result is that the JVM can't find the main class.
    APP_HOME=`cygpath --absolute --windows "$APP_HOME"`
    CLASSPATH=`cygpath --absolute --windows "$CLASSPATH"`
fi

if [ $msys = "true" ] ; then
    # tack it on to the end for MSys
    if ! echo "$PATH" | grep -q "$APP_HOME/gradle/bin" ; then
        export PATH="$APP_HOME/gradle/bin:$PATH"
    fi
fi

if [ $nonstop = "false" ] ; then
    # Increase the maximum file descriptors if we can.
    if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
        MAX_FD=`ulimit -H -n`
        if [ "$?" -eq 0 ] ; then
            ulimit -n $MAX_FD
        if [ "$?" -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
        fi
    fi
fi

# Set up the Max perm gen size for old JVMs
if [ "$GRADLE_OPTS" != "" ] ; then
    JVM_OPTS="$GRADLE_OPTS"
elif [ "`$JAVACMD -version 2>&1 | head -1 | cut -d' ' -f2 | cut -d'.' -f1`" -lt 9 ] ; then
    JVM_OPTS="$DEFAULT_JVM_OPTS -XX:MaxPermSize=256m"
else
    JVM_OPTS="$DEFAULT_JVM_OPTS"
fi

# Collect all arguments for the gradle command line
gradle_args=
for arg in "$@"
do
    gradle_args="$gradle_args \"$arg\""
done

eval exec "\"$JAVACMD\"" $JVM_OPTS \
    -classpath "\"$CLASSPATH\"" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
