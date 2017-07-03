ssh -t glowstone@185.116.156.30 "sudo systemctl stop glowstone"
ssh -t glowstone@185.116.156.30 "rm -rf glowstone.jar worlds/*"
sftp -b .circle/upload glowstone@185.116.156.30
ssh -t glowstone@185.116.156.30 "sudo systemctl start glowstone"

# sftp -b .circle/upload_jd leaf@glowstone.gserv.me
# ssh -t leaf@glowstone.gserv.me "./deploy_javadocs.sh"
