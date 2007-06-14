# AWK script for tabulating IPD data from Log4J log files.
# Daniel W. Dyer (dyerd01@csse.uwa.edu.au)
BEGIN { FS = " "}
  { print $8 "\t" $11 "\t" $13 "\t" $15 "\t" $17 "\t" $19 "\t" $20 "\t" $21 "\t" $22 "\t" $23 "\t" $24 "\t" $25 "\t" $26 "\t" $27 "\t" $28 "\t" $29 "\t" $30 "\t" $31 "\t" $32 "\t" $33 "\t" $34 "\t" $35 "\t" $36 "\t" $37 "\t" $38 "\t" $39 "\t" $40 "\t" $41 "\t" $42 "\t" $43 "\t" $44 "\t" $45 "\t" $46 "\t" $47 "\t" $48 "\t" $49 "\t" $50 "\t" $51}
END { }
