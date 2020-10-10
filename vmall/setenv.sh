#Heap Memory
export JAVA_OPTS="-Xms255m -Xmx255m -Xmn130m -XX:MaxMetaspaceSize=75m -XX:MetaspaceSize=75m -XX:SurvivorRatio=24"

#GC 
export JAVA_OPTS="$JAVA_OPTS -XX:+UseParallelOldGC -XX:-UseAdaptiveSizePolicy"

#GC Logging
#-XX:+PrintAdaptiveSizePolicy
export JAVA_OPTS="$JAVA_OPTS -XX:+PrintCommandLineFlags -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
export JAVA_OPTS="$JAVA_OPTS -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=10m"
#create volume vmll-info in Host, then mount it when run this container
export JAVA_OPTS="$JAVA_OPTS -Xloggc:/vmall-info/"

#Heap dump
export JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/vmall-info/"
