#/bin/sh

# Don't modify this file locally as this will be overwritten from the misc repo.
# Instead, modify the file in the misc repo:
# https://github.com/xl8-ai/misc/tree/master/github_templates/xl8_grpcprojects

usage() {
    echo "Usage: ./fetch_proto.sh [OPTIONS]"
    echo ""
    echo "Fetches the protobuf files as specified in .xl8_proto_dedendencies configuration file."
    echo ""
    echo "Options:"
    echo "    -b, --branch alex/feature    Download files built from another branch. (Default: master)"
    echo ""
    echo "Sample .xl8_proto_dedendencies file:"
    echo "    # PROJECT LANGUAGE PATH OPTIONS"
    echo "    e2e_pipe python e2e_pipe/api/"
    echo "    e2e_pipe proto proto/ junk-paths"
    echo ""
}

BRANCH=master

while [ "$1" != "" ]; do
    PARAM=$1
    case $PARAM in
        -h | --help)
            usage
            exit
            ;;
        --branch | -b)
            shift
            BRANCH=$1
            ;;
        *)
            echo "ERROR: Unknown option \"$PARAM\""
            usage
            exit 1
            ;;
    esac
    shift
done

if [ ! -f .xl8_proto_dedendencies ]; then
    echo "Please create .xl8_proto_dedendencies file first!"
    echo ""
    usage
    exit 1
fi

fetch() {
    PROJECT="$1"
    LANGUAGE="$2"
    TARGET="$3"
    unzip_opts="-o"
    [ "$4" = "junk-paths" ] && unzip_opts="-j -o"

    if [ -z $PROJECT ] || [ -z $LANGUAGE ] || [ -z $TARGET ]; then
        echo "# Invalid line: $@"
        echo ""
        return
    fi

    echo "# Fetching $PROJECT protocols for $LANGUAGE into $TARGET"

    URL="https://static.xl8.ai/proto/${BRANCH}/${PROJECT}_${LANGUAGE}.zip"
    TMP=$(mktemp)

    curl -s -S --fail $URL > $TMP
    if [ $? -eq 0 ]; then
        echo "Downloaded ${URL}"
        [ -d "$TARGET" ] || mkdir -p "$TARGET"
        unzip $unzip_opts -d "$TARGET" $TMP
    else
        echo "Failed to download ${URL}"
    fi
    rm $TMP
    echo ""
}

cat .xl8_proto_dedendencies | while read line || [ -n "$line" ]
do
    case $line in
        "#"*) continue ;;
        "") continue ;;
        *) fetch $line ;;
    esac
done
