#!/usr/bin/python
# Mila Netwhistler (c) 2006
#
# This is example ping script, he's getting IP-address as arg.
# You can write your own script in python or other languages. 
# RULES: IF NODE IS UP, script must return IP time in milliseconds, i.e. 0.48,
# otherwise (NODE IS DOWN) no result required.  

import sys, os
import string

def main():
    ip=res=sec=""
    if len(sys.argv)>1:
        ip = sys.argv[1]
    if len(ip)<1:
        print "Usage: fping.py IP-Address"
        sys.exit(1)
    cmd = os.popen("fping -a -r 1 -e -t 1  " + ip)
    for line in cmd.readlines():
        res=line.split()
        sec=str(res[1][1:])
	if len(sec)<6:
	    print sec
if __name__ == "__main__":
    main()
								
								