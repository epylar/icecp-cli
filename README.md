# icecp-tools

A collection of tools for operating on ICECP channels directly

### Install

Run `mvn install`; the `target` directory will have a zip file with easy-to-use
scripts to run the available commands. Alternately, download and install the RPM from [Artifactory](https://ubit-artifactory-or.intel.com/artifactory/simple/ARP-EAP-rpm-local/gateway-repo/icecp-tools/).

### Usage

Run the `icecp-cli` script with the following commands:
 - _execute_: execute a remote procedure call using the `-cmd` and `-param` options; prints the result of the execution
 - _load_: load a module from a JAR using the `-moduleUri` and `-configUri` options; prints the loaded module ID
 - _publish_: takes stdin lines and publishes them as bytes messages
 - _subscribe_: listens for messages on the given URI and prints them, undecoded, to stdout
 - _latest_: retrieves the latest message on a channel and prints it undecoded to stdout
 - _read_: retrieves the attribute value of a describable thing (see [Describable](https://github.intel.com/pages/iSPA/icecp-node/com/intel/icecp/core/Describable.html))
 - _write_: sets the attribute value of a describable thing (see [Describable](https://github.intel.com/pages/iSPA/icecp-node/com/intel/icecp/core/Describable.html))
 - _version_: prints the current version of the tool
 - _help_: prints available commands and options

Format: `./icecp-cli execute|load|publish|subscribe|latest|read|version|help [options]`

### Timeout System Property
 - Command timeout can be specified through the `-D name=value` option
 - Ex: `./icecp-cli ... -D timeoutInSec=60`
 - This property is optional and the default value of the timeout if not specified is 10s
 - This is useful if a particular module takes long to load due to a large JAR ( Ex: `icecp-module-storage`)

Examples:
 - To subscribe to a channel: `./icecp-cli subscribe -uri ndn:/some/channel`
 - To publish to a channel: `echo "...." | ./icecp-cli publish -uri ndn:/another/channel`
 - To execute a remote procedure call: `./icecp-cli execute -uri ndn:/intel/node/1234 -cmd doSomething -param '{"a": 1, "b": 2.0}'`
 - To load a module on the node specified by URI: `./icecp-cli load -uri ndn:/intel/node/1234 -moduleUri file:///path/to/jar -configUri file:///path/to/config/json`
 - To read a remote attribute: `./icecp-cli read -uri ndn:/intel/node/1234 -attributeName os`
 - To write a remote attribute: `echo "..." | ./icecp-cli write -uri ndn:/intel/node/1234 -attributeName os`
 - For verbose logging, use `./icecp-cli -debug ...`
 - For specifying configuration parameters, use `./icecp-cli ... -D uri=ndn-lab2.jf.intel.com -D foo=bar` (this example resets the chosen NFD to the lab server)

### Issues

 - format should be configurable
 - publish from stdin does not work like nc: if no input piped in, it should scan for keyboard input
 - clean up logging messages
 - depend only on channel classes, not all of node dependencies
 - when using `-D` for configuration, the properties may overlap; e.g. if DDS is expecting a `uri` property and so is NDN, there is no way to specify one for each (however, we are only using one channel at a time so this shouldn't be a big issue); if it is a concern we can add code to pull out the prefixes