#!/usr/bin/perl -wT

use Fcntl ':flock';

# START USER EDITS

# absolute path to folder files will be uploaded to.
# WINDOWS users, your path would like something like : images\\uploads
# UNIX    users, your path would like something like : /home/www/images/uploads
# do not end the path with any slashes and if you're on a UNIX serv, make sure
# you CHMOD each folder in the path to 777

$lockfileName = ".lock2";
$root = "/home/jryall/cgi-bin";
$logFile = "$root/uploadLog-study2.txt";
$uidMapFile = "$root/tagSEAUidMap-study2.txt";
$nextUidFile = "$root/tagSEANextUid-study2.txt";
$allConsentedUsers = "$root/tagSEAUsers-study2.txt";

# if you would like to be notified of uploads, enter your email address
# between the SINGLE quotes. leave this blank if you would not like to be notified
#$notify = '';

# UNIX users, if you entered a value for $notify, you must also enter your
# server's sendmail path. It usually looks something like : /usr/sbin/sendmail
#$send_mail_path = "";

# WINDOWS users, if you entered a value for $notify, you must also enter your
# server's SMTP path. It usually looks something like : mail.servername.com
#$smtp_path = "";


####################################################################
#    END USER EDITS
####################################################################

$OS = $^O; # operating system name
if($OS =~ /darwin/i) { $isUNIX = 1; }
elsif($OS =~ /win/i) { $isWIN = 1; }
else {$isUNIX = 1;}
	
if($isWIN){ $S{S} = "\\\\"; }
else { $S{S} = "/";} # seperator used in paths

use CGI; # load the CGI.pm module
my $GET = new CGI; # create a new object
my @VAL = $GET->param; #get all form field names

my($query_string) = "";
$query_string = $ENV{'QUERY_STRING'};
#$! = 500;
#die "quitting";

my($firstName);
my($lastName);
my($email_address);
my($job_function);
my($company_size);
my($company);
my($company_business);
my($anonymousStr);
my($uid) = -1;
my($anonymous) = 0;
my @params;

$firstName = $GET->param('firstName');
$lastName = $GET->param('lastName');
$email_address = $GET->param('email');
$company = $GET->param('company');
$job_function = $GET->param('jobFunction');
$company_business = $GET->param('companyBusiness');
$anonymousStr = $GET->param('anonymous');
$company_size = $GET->param('companySize');

if ($query_string =~ /^(.+)\&(.+)\&(.+)\&(.+)\&(.+)\&(.+)\&(.+)\&(.+)$/) {
	@params = ($1, $2, $3, $4, $5, $6, $7, $8);
}
#my $key;
#foreach $key (@params) {
#	if ($key =~ /^firstName=(.+)$/) {
#		$firstName = $1;
#	} elsif ($key =~ /^lastName=(.+)$/) {
#		$lastName = $1;
#	} elsif ($key =~ /^email=(.+)$/) {
#		$email_address = $1;
#	} elsif ($key =~ /^company=(.+)$/) {
#		$company = $1;
#	} elsif ($key =~ /^jobFunction=(.+)$/) {
#		$job_function = $1;
#	} elsif ($key =~ /^companyBusiness=(.+)$/) {
#		$company_business = $1;
#	} elsif ($key =~ /^anonymous=(.+)$/) {
#		$anonymousStr = $1;
#	} elsif ($key =~ /^companySize=(.+)$/) {
#		$company_size = $1;
#	}
#}

if (!($firstName && $lastName && $email_address))
{
	# error, query string is wrong
	print "Content-type: text/plain", "\n";
	print "Status: 501 Not Implemented. Missing required field", "\n\n";
#	print "Missing required field.", "\n"
	exit 501;
}

#print "Content-type: text/plain", "\n\n";
#print "$firstName $lastName $email_address.","\n";
#print "Status: 501 Not Implemented", "\n\n";	
#exit;
if ($anonymousStr =~ "true") {
	$anonymous = 1;
}

open(USERS, "+<$allConsentedUsers ") || die "Can't open Log File: $!\n"; 
seek USERS, 0, 2;
print USERS "$firstName\t$lastName\t$email_address\t$job_function\t$company\t$company_size\t$company_business\n";
close USERS;

if($anonymous != 1){
	# give them the same id as before
	my($old) = &checkExistance($firstName, $lastName, $email_address);
	if($old == -1){
		$uid = &getNewUID($firstName, $lastName, $email_address);
	}
	else{
		$uid = $old;
	}
}
else
{
	$uid = &getNewUID("anonymous", "anonymous", "anonymous");
}

if($uid != -1)
{
	print "Content-type: text/plain", "\n";
	print "Status: 200 OK", "\n\n";
	print "UID: $uid" . "\n";
	exit;
}
else
{
	print "Content-type: text/plain", "\n";
	print "Status: 501 Not Implemented", "\n\n";
	print "COULD NOT GET UID" . "\n";
	exit;
}


#################################################################### 

#################################################################### 

sub send_mail {
	my ($from_email, $from_name, $to_email, $to_name, $subject, $message ) = @_;
	
	if(open(MAIL, "|$CONFIG{mailprogram} -t")) {
		print MAIL "From: $from_email ($from_name)\n";
		print MAIL "To: $to_email ($to_name)\n";
		print MAIL "Subject: $subject\n";
		print MAIL "$message\n\nSubmitter's IP Address : $ENV{REMOTE_ADDR}";
		close MAIL;
		return(1);
	} else {
		return;
	}
}

#################################################################### 

#################################################################### 

sub send_mail_NT {
	
	my ($from_email, $from_name, $to_email, $to_name, $subject, $message ) = @_;
	
	my ($SMTP_SERVER, $WEB_SERVER, $status, $err_message);
	use Socket; 
    $SMTP_SERVER = "$CONFIG{smtppath}";                                 
	
	# correct format for "\n"
    local($CRLF) = "\015\012";
    local($SMTP_SERVER_PORT) = 25;
    local($AF_INET) = ($] > 5 ? AF_INET : 2);
    local($SOCK_STREAM) = ($] > 5 ? SOCK_STREAM : 1);
#    local(@bad_addresses) = ();
    $, = ', ';
    $" = ', ';

    $WEB_SERVER = "$CONFIG{smtppath}\n";
    chop ($WEB_SERVER);

    local($local_address) = (gethostbyname($WEB_SERVER))[4];
    local($local_socket_address) = pack('S n a4 x8', $AF_INET, 0, $local_address);

    local($server_address) = (gethostbyname($SMTP_SERVER))[4];
    local($server_socket_address) = pack('S n a4 x8', $AF_INET, $SMTP_SERVER_PORT, $server_address);

    # Translate protocol name to corresponding number
    local($protocol) = (getprotobyname('tcp'))[2];

    # Make the socket filehandle
    if (!socket(SMTP, $AF_INET, $SOCK_STREAM, $protocol)) {
        return;
    }

	# Give the socket an address
	bind(SMTP, $local_socket_address);
	
	# Connect to the server
	if (!(connect(SMTP, $server_socket_address))) {
		return;
	}
	
	# Set the socket to be line buffered
	local($old_selected) = select(SMTP);
	$| = 1;
	select($old_selected);
	
	# Set regex to handle multiple line strings
	#$* = 1;

    # Read first response from server (wait for .75 seconds first)
    select(undef, undef, undef, .75);
    sysread(SMTP, $_, 1024);
	#print "<P>1:$_";

    print SMTP "HELO $WEB_SERVER$CRLF";
    sysread(SMTP, $_, 1024);
	#print "<P>2:$_";

	while (/(^|(\r?\n))[^0-9]*((\d\d\d).*)$/gm) { $status = $4; $err_message = $3}
	if ($status != 250) {
		return;
	}

	print SMTP "MAIL FROM:<$from_email>$CRLF";

	sysread(SMTP, $_, 1024);
	#print "<P>3:$_";
	if (!/[^0-9]*250/m) {
		return;
	}

    # Tell the server where we're sending to
	print SMTP "RCPT TO:<$to_email>$CRLF";
	sysread(SMTP, $_, 1024);
	#print "<P>4:$_";
	/[^0-9]*(\d\d\d)/m;

	# Give the server the message header
	print SMTP "DATA$CRLF";
	sysread(SMTP, $_, 1024);
	#print "<P>5:$_";
	if (!/[^0-9]*354/m) {
		return;
	}

	$message =~ s/\n/$CRLF/igm;
	
	print SMTP qq~From: $from_email ($from_name)$CRLF~;
	print SMTP qq~To: $to_email ($to_name)$CRLF~;
#	if($cc){
#		print SMTP "CC: $cc ($cc_name)\n";
#	}
	print SMTP qq~Subject: $subject$CRLF$CRLF~;
	print SMTP qq~$message~;

	print SMTP "$CRLF.$CRLF";
	sysread(SMTP, $_, 1024);
	#print "<P>6:$_";
	if (!/[^0-9]*250/m) {
		return;
	} else {
		return(1);
	}

	if (!shutdown(SMTP, 2)) {
		return;
    } 
}

#################################################################### 

#################################################################### 

sub check_email {
	my($fe_email) = $_[0];
	if($fe_email) {
		if(($fe_email =~ /(@.*@)|(\.\.)|(@\.)|(\.@)|(^\.)|(\.$)/) ||
		($fe_email !~ /^.+@\[?(\w|[-.])+\.[a-zA-Z]{2,3}|[0-9]{1,3}\]?$/)) {
			return;
		} else { return(1) }
	} else {
		return;
	}
}
#################################################################### 

#################################################################### 

sub getNewUID {
	my($firstName, $lastName, $email_address) = @_;

	open(LOCKFILE, $lockfileName);
    	flock(LOCKFILE, LOCK_EX);

	open(NEXTUID, "<$nextUidFile") || die "Could not read user ids: $!\n"; 
	my($uid) = -1;
	my(@lines) = <NEXTUID>;
	my($line) = "";
	foreach $line (@lines)
	{
		if($line =~ m/^(\d+)$/)
		{
			$uid = $1;		   
			last;
		}
	}		
	close NEXTUID;

	my($nextUid) = $uid + 17;
	open(NEXTUID, ">$nextUidFile") || die "Could not save user id: $!\n"; 
	print NEXTUID $nextUid;		
	close NEXTUID;
	    	

	open(UIDMAP, "+<$uidMapFile") || die "Could not map user id: $!\n"; 	
	seek UIDMAP, 0, 2;
	print UIDMAP "$uid\t$firstName\t$lastName\t$email_address\n";	
	close UIDMAP;

	#create a directory for the user
	my($directory) = "$root/uploads/$uid";
	mkdir($directory);
	chmod(0777, "$directory");
	flock(LOCKFILE, LOCK_UN);
	close LOCKFILE;
	return $uid;

}
#################################################################### 

#################################################################### 

sub checkExistance {
	my($firstName, $lastName, $email_address) = @_;
	my($uid) = -1;

	open(LOCKFILE, $lockfileName);
    	flock(LOCKFILE, LOCK_EX);
    	
	open(UIDMAP, $uidMapFile) || die "Could not open user id map: $!\n"; 
	my(@lines) = <UIDMAP>;
	my($line) = "";
	foreach $line (@lines)
	{
		if($line =~ m/^(\d+)\t$firstName\t$lastName\t$email_address$/)
		{
			$uid = $1;
			last;
		}
	}
	close UIDMAP;

	flock(LOCKFILE, LOCK_UN);
	close LOCKFILE;
	return $uid;
}

#################################################################### 

#################################################################### 

sub log {
	open(LOCKFILE, $lockfileName);
	flock(LOCKFILE, LOCK_EX);
    	
	open(LOG, "+<$logFile") || die "Can't open Log File: $!\n"; 
    
	seek LOG, 0, 2;
	print LOG $_[0] . "\t\t";	

	my ($sec,$min,$hour,$mday,$mon,$year, $wday,$yday,$isdst) = localtime time;

	# update the year so that it is correct since it perl
	# has a 1900 yr offset	
	$year += 1900;

	# update the month since it is 0 based in perl	
	$mon += 1;
	
	printf LOG "%02d/%02d/%04d %02d:%02d:%02d\n", $mday, $mon, $year, $hour, $min, $sec;
	
	close LOG;
	
	flock(LOCKFILE, LOCK_UN);
    close LOCKFILE;
}
