#!/usr/bin/python
# Mila Netwhistler (c) 2006
#
# This script getting 3 arg: IP-address, monitoring OID name and OID value.
# Then you can do what you want with this results (Example writes result in file).
# You can write your own script in python or other languages.

import sys, os
import string

def main():
    ip=oid=val=""
    if len(sys.argv)>3:
        ip = sys.argv[1]
        oid = sys.argv[2]
	val = sys.argv[3]
    if len(ip)<1 or len(oid)<1:
        print "Usage: getsnmp.py IP-Address  OID VALUE"
        sys.exit(1)
    f = open("/tmp/" + ip + ".log","w")
    f.write(ip + ":" + oid + ":" + val)
    f.close()

if __name__ == "__main__":
    main()
								
								