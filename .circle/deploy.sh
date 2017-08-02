ssh -t glowstone@185.116.156.30 "sudo systemctl stop glowstone" -o ConnectTimeout=15
ssh -t glowstone@185.116.156.30 "rm -rf glowstone.jar worlds/*" -o ConnectTimeout=15
sftp -b .circle/upload glowstone@185.116.156.30 -o ConnectTimeout=15
ssh -t glowstone@185.116.156.30 "sudo systemctl start glowstone" -o ConnectTimeout=15
