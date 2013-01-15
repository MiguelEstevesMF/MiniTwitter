# Load LWP
use LWP::UserAgent;
use XML::Simple;
use JSON qw( decode_json );

my $requestCount = 1;
 
# Create a user agent
my $ua = LWP::UserAgent->new();

my $base_url = "https://jbossserver-uzowebsms.rhcloud.com/MiniTwitter";

print "Base url set to $base_url \n\n";

# Make output directory
$output_dir = 'output';
mkdir($output_dir);


# Requests

$d = "Admin cleans database";
send_http_get_request($d,"/user/delete-all.xml?key=admin");

$d = "Admin checks his info";
send_http_get_request($d,"/user.json?key=admin","admin-user-info.json");

$d = "Admin writes: Today is MiniTwitter startup. Free beer for everybody!";
send_http_get_request($d,"/write-message.json?key=admin&text=Today is MiniTwitter startup. Free beer for everybody!");

$d = "Admin writes: \@admin I'm feeling lonely!";
send_http_get_request($d,"/write-message.json?key=admin&text=\@admin I'm feeling lonely!");

$d = "Admin creates user cat";
$cat_key = send_http_get_request($d,"/user/create.json?key=admin&username=cat&name=caterine");

$d = "Admin writes: \@cat Hello kitty!";
send_http_get_request($d,"/write-message.json?key=admin&text=\@cat Hello kitty!");

$d = "Cat writes: \@admin Miau!";
send_http_get_request($d,"/write-message.json?key=$cat_key&text=\@admin Miau!");

$d = "Admin begins following cat";
send_http_get_request($d,"/user/follow.json?key=admin&username=cat");

$d = "Cat begins following Admin";
send_http_get_request($d,"/user/follow.json?key=$cat_key&username=admin");

$d = "Admin attempts to create a user with same username as cat but fails";
send_http_get_request($d,"/user/create.xml?key=admin&username=cat");

$d = "Cat attempts to see user mouse info but fails";
send_http_get_request($d,"/user.json?key=$cat_key&username=mouse");

$d = "Cat attempts to create mouse but fails";
send_http_get_request($d,"/user/create.json?key=$cat_key&username=mouse");

$d = "Admin creates user mouse";
$mouse_key = send_http_get_request($d,"/user/create.json?key=admin&username=mouse&name=mouser");

$d = "Cat writes: \@mouse Miau!";
send_http_get_request($d,"/write-message.json?key=$cat_key&text=\@mouse Miau!");

$d = "Cat begins following mouse";
send_http_get_request($d,"/user/follow.json?key=$cat_key&username=mouse");

$d = "Mouse writes: \@admin go to hell!";
send_http_get_request($d,"/write-message.json?key=$mouse_key&text=\@admin go to hell!");

$d = "Mouse begins following admin";
send_http_get_request($d,"/user/follow.json?key=$mouse_key&username=admin");

$d = "Admin attempts to follow god but fails";
send_http_get_request($d,"/user/follow.json?key=admin&username=god");

$d = "Mouse writes: \@cat can't touch me! but fails to set the correct key for authentication";
send_http_get_request($d,"/write-message.json?key=123text=\@cat can't touch me!","401.html");

$d = "Mouse attempts to unfollow admin but fails to write the proper admin username";
send_http_get_request($d,"/user/follow.json?key=$mouse_key&username=123");

$d = "Admin creates user dog";
$dog_key = send_http_get_request($d,"/user/create.json?key=admin&username=dog&name=doggy");

$d = "Cat writes: \@dog Miau!";
send_http_get_request($d,"/write-message.json?key=$cat_key&text=\@dog Miau!");

$d = "Mouse writes: \@cat I'll get you now!";
send_http_get_request($d,"/write-message.json?key=$mouse_key&text=\@cat I'll get you now!");

$d = "Dog writes: \@cat WOOF!";
send_http_get_request($d,"/write-message.json?key=$dog_key&text=\@cat WOOF!");

$d = "Dog writes: I'm hungry!";
send_http_get_request($d,"/write-message.json?key=$dog_key&text=I'm hungry!");

$d = "Dog begins following cat";
send_http_get_request($d,"/user/follow.json?key=$dog_key&username=cat");

$d = "Dog begins following admin";
send_http_get_request($d,"/user/follow.json?key=$dog_key&username=admin");

$d = "Dog begins following mouse";
send_http_get_request($d,"/user/follow.json?key=$dog_key&username=mouse");

$d = "Dog checks his info";
send_http_get_request($d,"/user.xml?key=$dog_key","dog-user-info.xml");

$d = "Cat writes: Yawn!";
send_http_get_request($d,"/write-message.json?key=$cat_key&text=Yawn!");

$d = "Cat checks users followed by and following himself";
send_http_get_request($d,"/users/followed-by-and-following.xml?key=$cat_key","cat-checks-users-followed-by-and-following-cat.xml");

$d = "Dog checks users followed by and following admin";
send_http_get_request($d,"/users/followed-by-and-following.xml?key=$dog_key&username=admin","dog-checks-users-followed-by-and-following-admin.xml");

$d = "Dog unfollows admin";
send_http_get_request($d,"/user/unfollow.json?key=$dog_key&username=admin");

$d = "Admin checks users followed by and following admin";
send_http_get_request($d,"/users/followed-by-and-following.json?key=admin","admin-checks-users-followed-by-and-following-admin.json");

$d = "Cat attempts to follow cat but fails";
send_http_get_request($d,"/user/follow.json?key=$cat_key&username=cat");

$d = "Cat attempts to follow mouse but fails";
send_http_get_request($d,"/user/follow.json?key=$cat_key&username=mouse");

$d = "Cat attempts to unfollow dog but fails";
send_http_get_request($d,"/user/unfollow.json?key=$cat_key&username=dog");

$d = "Admin checks all messages";
send_http_get_request($d,"/messages-all.json?key=admin","admin-checks-all-messages.json");

$d = "Cat checks messages of cat addressed at self or followers of cat";
send_http_get_request($d,"/messages.json?key=$cat_key","cat-checks-messages-of-cat-addressed-at-self-or-followers-of-cat.json");

$d = "Cat checks messages of cat addressed at self or followers of cat and with search=\@admin";
send_http_get_request($d,"/messages.json?key=$cat_key&search=\@admin","cat-checks-messages-of-cat-addressed-at-self-or-followers-of-cat-with-search-admin.json");

$d = "Admin checks messages of admin addressed at self or followers of admin";
send_http_get_request($d,"/messages.xml?key=admin","admin-checks-messages-of-admin-addressed-at-self-or-followers-of-admin.xml");





# Procedure to send http request and store the response in a file
sub send_http_get_request {

	my $desc = $_[0];
	my $relative_url = $_[1];
	my $url = $base_url . $_[1];

	print "\n----------\nRequest " . $requestCount++ . ": $desc\n";
	print "To: $relative_url \n";

	# Perform the request
	my $response = $ua->get("$url", 'Accept' => '*/*');

	# Check for HTTP error codes
	print "Http status: " . $response->code . " " . $response->message . "\n";

	# Print content to file
	if (defined $_[2]) {
		print_to_file($_[2],$response->content,'>');
		
	} else {
		# Parse XML result
		if ($response->content_type eq "application/xml") {
		
			$xml_parser = new XML::Simple();
			$data = $xml_parser->XMLin($response->content);
		
			if($result = $data->{'result'}->{'content'}) {
				print "Result: $result\n";
				return $result;
			} else {
				$error = $data->{'message'};
				print "Error: $error\n";
				print_to_file("error_log.xml",$response->content,'>>');
				return $error;
			}
		}
		
		# Parse JSON result
		if ($response->content_type eq "application/json") {
		
			$data = decode_json($response->content);
		
			if($result = $data->{'singleResult'}->{'result'}) {
				print "Result: $result\n";
				return $result;
			} else {
				$error = $data->{'errorResult'}->{'message'};
				print "Error: $error\n";
				print_to_file("error_log.json",$response->content,'>>');
				return $error;
			}
		}
	}
}

sub print_to_file {
	my $file = $output_dir . "/" . $_[0];
	my $data = $_[1];
	my $opt = $_[2];
	open (FILE,  $opt . $file) or die("File $file will not open!");;
	print FILE $data;
	close (FILE); 
	print "Output to file $file\n";
}