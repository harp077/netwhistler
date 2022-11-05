#!/usr/bin/python
# Mila Netwhistler (c) 2006
#
# This is example ping script, he's getting IP-address as arg.
# You can write your own script in python or other languages.
# RULES: IF MAC IS RECOGNIZED, script must return mac string, i.e. C9:C0:40:89:77:B2,
# otherwise no result required.

import sys, os
import string

def main():
    ip=""
    if len(sys.argv)>1:
        ip = sys.argv[1]
    if len(ip)<1:
        print "Usage: getmac.py IP-Address"
        sys.exit(1)
    cmd = os.popen("arp -a " + ip)
    for line in cmd.readlines():
        res=line.split()
        print res[3]

if __name__ == "__main__":
    main()
								
								