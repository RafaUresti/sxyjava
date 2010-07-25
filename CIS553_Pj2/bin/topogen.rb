# topogen -- generate a topology file for Fishnet
#
# Use command line arguments to specify general parameters
#
# -n nodes		number of nodes to connect, default 3
# -r [density]	connect randomly, with (roughly) average density links per node
# -s			connect serially (1 - 2 - 3 - 4), default
# -c			connect in a loop
# -l loss-rate	every link has the same loss rate (0 .. 1), default 0
# -f MTTF		each node fails on average after MTTF seconds, default no-fail
# -m MTTR		failed nodes recover in an average of MTTR seconds, default 1
# -d delay		every link has delay latency, default 1 millisecond
# -b bw		every link has bw bandwidth in KB/s, default 10
# -a			randomize all link delays and bandwidths, 0..delay, 0..bw
# -z seed		initialize the random # generator with seed

require 'getoptlong'

# alias to save some typing
ReqArg = GetoptLong::REQUIRED_ARGUMENT
OptArg = GetoptLong::OPTIONAL_ARGUMENT
NoArg = GetoptLong::NO_ARGUMENT

NumberOption = "--number"
RandomOption = "--random"
SerialOption = "--serial"
CircleOption = "--circle"		# simple loop of edges
LossOption = "--loss-rate"
FailOption = "--mttf"
RecoverOption = "--mttr"
DelayOption = "--delay"
BwOption = "--bandwidth"
AllOption = "--all-random"
SeedOption = "--seed"

opts = GetoptLong.new(
	[NumberOption, "-n", ReqArg],
	[RandomOption, "-r", OptArg],
	[SerialOption, "-s", NoArg],
	[CircleOption, "-c", NoArg],
	[LossOption, "-l", ReqArg],
	[FailOption, "-f", ReqArg],
	[RecoverOption, "-m", ReqArg],
	[DelayOption, "-d", ReqArg],
	[BwOption, "-b", ReqArg],
	[AllOption, "-a", NoArg],
	[SeedOption, "-z", ReqArg]
	)

# set the defaults, before processing the argument list
numNodes = 3
serial = true
circle = false
density = 5.0
$lossRate = 0
mttf = 0
mttr = 1
$delay = 1			# in milliseconds
$bw = 10			# in KB/s
$allRandom = false

# Print an edge, with link info such as what fraction of packets to drop
def putEdge(from, to)
   $stdout << "edge " << from << " " << to
   $stdout << " lossRate " << $lossRate if $lossRate > 0
   if $allRandom then
	   $stdout << " delay " << rand($delay) + 1 << 
			" bw " << rand($bw) + 1
   else
	   $stdout << " delay " << $delay if $delay != 1
	   $stdout << " bw " << $bw if $bw != 10
    end
    $stdout << "\n" 
end

# Print a command to fail or restart a node or edge
# We first put out the time, to allow for a dynamically changing topology
def putAction(actionStr, node, time)
	$stdout << "time " << time << "\n"
	$stdout << actionStr << node << "\n"
end

# sample usage
# ARGV = ["-f", 1, "-z", 2]

# process each command line parameter
opts.each do |opt, arg|
	case opt
		when NumberOption
			numNodes = arg.to_i
		when RandomOption
			serial = false
			density = arg.to_f
		when SerialOption
			serial = true
		when CircleOption
			circle = true
			serial = true
		when LossOption
			$lossRate = arg.to_f
			raise ArgumentError unless 
						$lossRate >= 0 && $lossRate <= 1
		when FailOption
			mttf = arg.to_i
		when RecoverOption
			mttr = arg.to_i
		when DelayOption
			$delay = arg.to_i
		when BwOption
			$bw = arg.to_i
		when AllOption
			$allRandom = true
		when SeedOption
			srand(arg.to_i)
		else
			raise ArgumentError 	# shouldn't get here!
		end
	end

# output our parameters, for record-keeping
# the parser in commands.rb will ignore these
$stdout << "//// Topology generated with " <<
	"\n//// numNodes: " << numNodes <<
	"\n//// topology type: " << (circle ? "circle" : 
				(serial ? "serial" : "random")) <<
	"\n//// link density: " << density <<
	"\n//// lossRate: " << $lossRate.to_s <<
	"\n//// mttf/mttr: " << mttf << " " << mttr <<
	"\n//// delay/bw: " << $delay << " " << $bw <<
	"\n//// all-random: " << $allRandom.to_s << "\n"

if serial then
	# this is either a simple linked topology, or a simple circle
	(0...numNodes).each { |x|
		putEdge(x, x+1) unless x == numNodes - 1
		}
	if circle then
		putEdge(0, numNodes - 1)
	end
else
# random attach.  Make sure everyone is attached to someone (who in turn
# is attached to the rest of the graph), and then attach them to other nodes
# with probability p = density/(x + y), for nodes x, y
	(1...numNodes).each { |x|
		putEdge(first = rand(x), x)
		(0...x).each { |y|
			putEdge(y, x) if y != first && rand() < density/(x+y)
			}
		}
end

# if we are to generate node failures, then we walk through time,
# and at every millisecond, we check if someone is either to be killed or restarted
# keep this up until everyone has failed on average 5 times
if (mttf > 0) then
	simTime = mttf * 5
	mttfRate = 1.0/mttf
	mttrRate = 1.0/mttr
	failList = []					# keep track of whose failed
	(1..(simTime*1000)).each { |t|
		(0...numNodes).each { |n|
			randNum = rand() * 1000
			if (failList.include? n) then
				if (randNum < mttrRate) then
					failList.delete(n)
					putAction("restart ", n, t)
				end
			elsif (randNum < mttfRate) then
				failList.push(n)
				putAction("fail ", n, t)
			end
		}
	}
end

