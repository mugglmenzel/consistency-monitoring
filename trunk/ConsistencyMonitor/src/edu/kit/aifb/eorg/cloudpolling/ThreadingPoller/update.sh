cd /home/ec2-user/monitoring;
rm starter.jar;
curl http://dl.dropbox.com/u/3128579/MonitoringStarter.jar > starter.jar;
chown ec2-user:ec2-user starter.jar;
chmod 755 starter.jar;
rm log4j.properties;
curl http://dl.dropbox.com/u/3128579/log4j.properties > log4j.properties;
chown ec2-user:ec2-user log4j.properties;
chmod 755 log4j.properties;