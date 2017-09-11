# Impala Profile Parser

Sample project to parse Impala thrift encoded query profile.

## Getting Started

Clone the repository and build it using maven.

```
git clone git@github.com:kr-arjun/impala-profile-parser.git
cd impala_query_parser/
mvn clean install
```
### Usage

```
 java -jar target/impala-profile-parser-jar-with-dependencies.jar <query profile file> <optional outout file>
 
 If output file argument is omitted , parsed profile will be written to STDOUT.
 
``` 
### Sample:
 ```
 
 $ java -jar target/impala-profile-parser-jar-with-dependencies.jar /opt/mapr/impala/impala-2.5.0/logs/profiles/impala_profile_log_1.1-1504284977644
Sep 11, 2017 12:01:19 AM com.arjun.impala.ProfileParser parse
INFO: Processing profile 1
Sep 11, 2017 12:01:19 AM com.arjun.impala.ProfileParser parse
INFO: Processing profile 2
Sep 11, 2017 12:01:19 AM com.arjun.impala.ProfileParser parse
INFO: Processing profile 3
Sep 11, 2017 12:01:19 AM com.arjun.impala.ProfileParser parse
INFO: Processing profile 4


*******************************************************************

Profile:1
QueryId: b34de225bb7bbe0b:3b554c5c85eb759f
querystartTime: 2017-09-09 15:39:43.117327000
queryEndTime: 2017-09-09 15:39:43.821870000
queryType: DDL
queryStatus: FINISHED
user: mapr
defaultDb: default
queryString: show tables
...
$
```
