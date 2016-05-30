# Virtual Call Resolver

In order to constructs the call graph of object oriented programs, we first need to resolve the virtual calls. 
The goal of this project is to implement four different types of virtual call resolution algorithms, including:

* Reachability Analysis (RA)
* Class Hierarchy Analysis (CHA)
* Rapid Type Analysis (RTA)
* Context-insensitive Control Flow Analysis (0-CFA)

## Implementation:
This project is implemented on top of [Soot] static analysis framework. All four algorithms provides a common interface:

```java
public ResolvedVirtualCall resolveCall(JVirtualInvokeExpr virtualCall);
```

Which, for a given virtual call, returns a ResolvedVirtualCall object containing the set of available types (classes) at that call. The Main class traverses the target java class (class under analysis) and calls the above method upon visiting a virtual call and prints out the resolved call.

## How to run:
Run the Main class (Main.java), with the appropriate options:

```sh
usage: cgg
 -alg,--algorithm <arg>   the name of algorithm (RA, CHA, RTA, 0-CFA)
 -cls,--class <arg>       target class to calculate call graph
 -cp,--classPath <arg>    directory containing the analysis classes
 -pkg,--package <arg>     package prefix of the classes under analysis
 -t,--time                calculate performance
 -v,--verbose <arg>       show process results (e.g. constraint graph)
```

A sample command could be:
```sh
java Main -alg 0-CFA -cp $Test_project_root_dir$/src -pkg com.example.test -cls Main -v $output_dir$ -t
```


[Soot]:https://github.com/Sable/soot

