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
<daemon-1 internal ip>
<daemon-2 internal ip>
<daemon-3 internal ip>
<daemon-4 internal ip>
<daemon-5 internal ip>
<daemon-6 internal ip>
$ java -jar TCPHostsServer.jar
```

# App

```shell
$ sudo apt-get update && sudo apt-get install -y openjdk-17-jdk git make
$ git clone https://github.com/VladProg/parcs-java-sort.git
$ cd parcs-java-sort
$ echo <hosts-server internal ip> > out/server
$ make generate
$ make run WORKERS=<number of workers> < out/input-x.txt > <output file name>
```
