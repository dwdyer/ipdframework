# AWK script for calculating IPD means and standard deviations.
# Daniel W. Dyer (dyerd01@csse.uwa.edu.au)
BEGIN { FS = "\t"}
        {count = count + 1
	 total_av = total_av + $2
	 total_best = total_best + $3
	 total_against_fixed = total_against_fixed + $4
         total_fixed = total_fixed + $5
         total_ad = total_ad + $8
         total_stft = total_spav + $17
         total_stft = total_stft + $18
         total_stupid = total_stupid + $23
         total_grim = total_grim + $32
         total_pav = total_pav + $33
         total_tft = total_tft + $34
         total_ac = total_ac + $39

         total_adv = total_adv + $8 + $12 + $16 + $20
         total_acv = total_acv + $36 + $37 + $38 + $39
}
END { print "\n------------------------------------------------------------"
      print "Av: " total_av/count "   Best: " total_best/count "   Fixed Player: " total_fixed/count "   Against Fixed: " total_against_fixed/count
      print "--------------------------------------------------------------"
      print "(00)Always Cooperate:\t\t" total_ac/count
      print "(09)Suspicious Pavlov:\t\t" total_spav/count
      print "(10)Suspicious Tit-For-Tat:\t" total_stft/count
      print "(15)Stupid:\t\t\t" total_stupid/count
      print "(24)Grim:\t\t\t" total_grim/count
      print "(25)Pavlov:\t\t\t" total_pav/count
      print "(26)Tit-For-Tat:\t\t" total_tft/count
      print "(31)Always Defect:\t\t" total_ad/count
      print "----------------------------------------"
      print "Always Defect & Variants:\t" total_adv/count
      print "Always Cooperate & Variants:\t" total_acv/count
}
