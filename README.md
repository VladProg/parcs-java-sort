# Instructions

# Google Cloud

```shell
$ gcloud projects create parcs-java-sort
$ gcloud config set project parcs-java-sort
$ gcloud config set compute/zone us-central1-c
$ gcloud compute instances create daemon-1 daemon-2 daemon-3 daemon-4 daemon-5 daemon-6 hosts-server app
```

# Daemons

```shell
$ sudo apt-get update && sudo apt-get install -y openjdk-17-jdk
$ wget https://github.com/lionell/labs/raw/master/parcs/Daemon/Daemon.jar
$ java -jar Daemon.jar
```

# Hosts server

```shell
$ sudo apt-get update && sudo apt-get install -y openjdk-17-jdk
$ wget https://github.com/lionell/labs/raw/master/parcs/HostsServer/TCPHostsServer.jar
$ cat > hosts.list
$daemon_1_internal_ip
$daemon_2_internal_ip
$daemon_3_internal_ip
$daemon_4_internal_ip
$daemon_5_internal_ip
$daemon_6_internal_ip
$ java -jar TCPHostsServer.jar
```

# App

```shell
$ sudo apt-get update && sudo apt-get install -y openjdk-17-jdk git make
$ git clone https://github.com/VladProg/parcs-java-sort.git
$ cd parcs-java-sort
$ echo $hosts_server_internal_ip > out/server
$ make run WORKERS=$number_of_workers <<< "$n $seed" > $output_file_name
```
