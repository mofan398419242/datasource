部署方式
1. 我已经更改flume-hbase-solr.conf-sx.template文件，在里面添加了分发数据至 10.210.118.40 端口号12321 。请将这个文件部署替换现有flume配置文件。
1. copy jar文件放置部署目录。部署flume只需放置在10.210.118.40这台机器。
2. 在flume的conf/flume-env.sh里面将jar文件加入classpath
3. 更改template文件里面的folder_prefix成对应hdfs文件路径。flume-http-traffic-table.template里面需要将your_directory替换成所对应log文件路径。
3. 启动方式，输入命令：${FLUME_HOME}/bin/flume-ng agent --conf ${FLUME_HOME}/conf --conf-file ${template_file} --name ${template_name}

多表的template_file ： flume-mutiple-table.template
多表的template_name : ndelogCollector

http流量日志的template_file : flume-http-traffic-table.template
http流量日志的template_name : httpTraffic