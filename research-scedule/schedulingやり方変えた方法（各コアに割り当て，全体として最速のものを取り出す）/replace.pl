#! /usr/bin/perl


$inputfilename = $ARGV[0];
$outfilename = $ARGV[1];

if(open(IN, "$inputfilename")){
	if(open(OUT,">$outfilename")){
		while($line = <IN>){
			print $line;
			$line =~ s/ +//;
			$line =~ s/ +/\t/g;
			
			print OUT $line;
		}
	}
	}
else {
print "don't open file";
}


