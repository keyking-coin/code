pplication
DIALOG=${DIALOG=dialog}
ulimit -u 10240
ulimit -a
APP_DIR=$(cd "$(dirname "$0")";cd ..; pwd)
echo $APP_DIR
APP_NAME="coin"
PIDFILE="$APP_DIR/bin/pid.$APP_NAME"
# Resolve the location of the 'ps' command
PSEXE="/bin/ps"
DATE="`date`"
export CLASSPATH=$APP_DIR/coin.jar:$APP_DIR/conf/
for jarz in $APP_DIR/lib/*.jar
do CLASSPATH=$CLASSPATH:$jarz
done
export CLASSPATH
echo 'classpath is [' $CLASSPATH ']'
cd $APP_DIR


getpid() {
 
    if [ -f "$PIDFILE" ]
    then
        if [ -r "$PIDFILE" ]
        then
            pid=`cat "$PIDFILE"`
            if [ "X$pid" != "X" ]
            then
                    realpid=`$PSEXE -p $pid | grep $pid | grep -v grep | awk '{print $1}' | tail -1`
                    if [ "$pid" != "$realpid" ]
                    then
                        echo "Delete $PIDFILE because $pid does not exist."
                        rm -f "$PIDFILE"
                        pid=""
                    fi
            fi
        else
            echo "Cannot read $PIDFILE."
            exit 1
        fi
    fi
}
start() {
    echo "Starting $APP_NAME..."
    getpid
    if [ "X$pid" = "X" ]
    then
			ucho 'prepare to run the MAIN now.'
       # The string passted o eval must handles spaces in paths correctly.
        nohup java -classpath $CLASSPATH -Xdebug -Xrunjdwp:transport=dt_socket,address=42105,server=y,suspend=n com.keyking.coin.service.Service > $APP_DIR/bin/console.log &
        newpid=$!
        echo "Start $APP_NAME at $DATE, which PID $newpid"
        echo "$newpid" > "$PIDFILE"
    else
        echo "$APP_NAME is already running."
        exit 1
    fi
}

stop() {
    echo "Stopping $APP_NAME..."
    getpid
    if [ "X$pid" = "X" ]
    then
        echo "$APP_NAME was not running."
    else
        kill $pid
        sleep 20
        getpid
        if [ "X$pid" = "X" ]
        then
            # An explanation for the failure should have been given
            echo "Successfully stop $APP_NAME."
            exit 0
        else
            echo "Unable to stop $APP_NAME."
        fi
    fi
}


menu()
{
        echo " Choose your operation:"
        echo " 1.Start your application"
        echo " 2.Stop your application"
        echo " 3.Restart your application"
        echo " 4.Exit"
}

while :;
do
        menu
        read -p "Enter your choice: " choice
        case $choice in
                1)
                        start
                        exit 0
                        ;;
                2) 
                        stop
                        exit 0
                        ;;
                3)
                                                start
                        stop
                        exit 0
                        ;;
                4)
                        exit 0
                        ;;
                *)
                echo " Ivalid choice!!!"
        esac
done
