ssh -o ConnectTimeout=15 -t glowstone@185.116.156.30 "sudo systemctl stop glowstone"
ssh -o ConnectTimeout=15 -t glowstone@185.116.156.30 "rm -rf glowstone.jar worlds/*"
sftp -o ConnectTimeout=15 -b .circle/upload glowstone@185.116.156.30
ssh -o ConnectTimeout=15 -t glowstone@185.116.156.30 "sudo systemctl start glowstone"
