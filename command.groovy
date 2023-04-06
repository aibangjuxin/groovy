def command = "ls -l"
def output = command.execute().text
println output
println "hello"
println "${output}"


println "------------------------------------------------"
def fileName = "abcd.json"

// curl -s -H "Accept: application/json" {{url}} -o {{filename.json}}
def approvalCommand = "curl -w '%{http_code}' https://www.baidu.com -o $fileName"
println "${approvalCommand}"

println "execute approval command"
approvalCommand.execute().text

def file = new File("/Users/lex/shell/groovy/abcd.json")
file.eachLine { line ->
    println line
}
println "------------------------------------------------"

println "--equalsIgnoreCase-"

String str1 = "Hello";
String str2 = "hello";

if (str1.equalsIgnoreCase(str2)) {
    System.out.println("The two strings are equal, ignoring case.");
} else {
    System.out.println("The two strings are not equal, ignoring case.");
}
println 'The next will be get the status of the approvalcommand output.';
def httpCode = approvalCommand.execute().text.trim()


println "${httpCode}"
// attempt '200' change to 200
def lex = httpCode.replaceAll("'","")

println "${lex}"
def str = "\"Hello, World!\""
def result = str.replaceAll("\"", "")
println result


println "dddddddddddddddddddddddddddddddddddddd"

if ("200".equalsIgnoreCase(httpCode)) {
    println "The response code is 200, ignoring case."
    } else {
        println("The response code is not 200, ignoring case.");
}

println "${httpCode}"
if(httpCode == "200") {
  // Do something if the HTTP code is 200
  println "The status is 200"
} else {
  // Do something else if the HTTP code is not 200
  println "The status is not 200"
}


println "groovy call shell script"
/*
在Groovy中，您可以使用`ProcessBuilder`类来调用Shell脚本。以下是一个示例：

```groovy
def commandcall = ["sh", "/path/to/script.sh", "arg1", "arg2"]
def processBuilder = new ProcessBuilder(commandcall)
def process = processBuilder.start()
process.waitFor()

def output = process.inputStream.text
def error = process.errorStream.text

println "Output: $output"
println "Error: $error"
```

在这个例子中，`command`变量包含要执行的Shell脚本的名称和参数。`ProcessBuilder`类用于创建一个进程，该进程将执行Shell脚本。`start()`方法用于启动进程。`waitFor()`方法用于等待进程完成。

一旦进程完成，您可以使用`inputStream`和`errorStream`属性来获取输出和错误信息。`text`属性用于将输出和错误信息转换为字符串。

最后，您可以使用`println`语句将输出和错误信息打印到控制台。
*/
def commandcall = ["sh", "/Users/lex/shell/groovy/c.sh"]
def processBuilder = new ProcessBuilder(commandcall)
def process = processBuilder.start()
process.waitFor()

def calloutput = process.inputStream.text
def callerror = process.errorStream.text

println "Output: $calloutput"
println "Error: $callerror"

class Example {
   static void main(String[] args) {
      // Using a simple println statement to print output to the console
      println('Hello World');
   }
}


public class Demo { 
    public static void main(String[] args) { 
        System.out.println("Hello World"); 
        println "abc"
        } 
    }

println "Finished"
println "hello" /* a multiline comment starting
                   at the end of a statement */
println 1 /* one */ + 2 /* two */

def jq_home() {
    println "jqddddddddddddddddddd"
}

jq_home()

def name = 'Guillaume' // a plain string
def greeting = "Hello ${name}"

println "${greeting}"

/**
 * A Class description
 */
class Person {
    /** the name of the person */
    String name

    /**
     * Creates a greeting method for a certain person.
     *
     * @param otherPerson the person to greet
     * @return a greeting message
     */
    String greet(String otherPerson) {
       "Hello ${otherPerson}"
    }
}

println 2 /* one */ + 3 /* two */


// def httpCode = sh(returnStdout: true, script: "$approvalCommand").toString().trim()
// /usr/local/bin/bash ("cat $fileName")
// println "${httpCode}"

/*

import org.apache.commons.validator.routines.EmailValidator

def email = "lex@abc.com"
def validator = EmailValidator.getInstance()

if (validator.isValid(email)) {
    println("Valid email address")
} else {
    println("Invalid email address")
}

groovy command.groovy

def httpCode = approvalCommand.execute().text.trim()



brew install groovy
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home
#export JAVA_HOME=/Library/Internet\ Plug-Ins/JavaAppletPlugin.plugin/Contents/Home
PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/lib:$JAVA_HOME/jre/bin

groovy --version
Groovy Version: 4.0.10 JVM: 11.0.10 Vendor: Oracle Corporation OS: Mac OS X

def output = "ls -l".execute().text
println output


The execute() method is not a built-in method of the Groovy language. 
It is likely that the execute () method is a custom method defined in the approvalCommand object Or library that is being used in the code. However, if you are referring to the execute () method of the String class in Groovy, which is used to execute a command in the shell, 
it is available in Groovy version 1.5 and later. Here is an example of using the execute () method in Groowy:
execute（）方法不是Groovy语言的内置方法。execute（）方法很可能是在代码中使用的approvalCommand对象或库中定义的自定义方法。然而，如果您引用的是Groovy中String类的execute（）方法，该方法用于在shell中执行命令，那么它在Groovy 1.5版及更高版本中可用。以下是在Groowy中使用execute（）方法的示例：
*/
