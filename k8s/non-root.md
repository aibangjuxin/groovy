Here are the explanations of groupadd and useradd commands in Markdown format:

## groupadd 

The groupadd command creates a new group in Linux. Some useful options include:

- -g: Assigns the specified GROUPID number to the new group account. Must be a non-negative integer below 2147483648.   
- -r: Creates a system account. Creates a group with a User ID less than NEW_GID_MIN or greater than NEW_GID_MAX from login.defs.

For example: 
groupadd -g 4000 developers  creates a new group named developers with GID 4000.

## useradd

The useradd command creates a new user in Linux. Some useful options include:  

- -m: Create the user's home directory and copy the files from the skeleton directory (/etc/skel).   
- -l: Don't create a group with the same name as the user, use the DEFAULT_GROUP.    
- -c: Comment for the user account. Usually the user's full name.  
- -u: Assigns the specified USERID number to the new user account. Must be a non-negative integer below 2147483648.   
- -s: The name of the user's login shell. The default is /bin/sh.   
- -r: Creates a system user. Creates a user with a User ID less than UID_MIN or greater than UID_MAX from login.defs.  

For example: 
useradd -m -c "John Doe" -s /bin/bash john
creates a new user named john, with a comment of "John Doe", login shell of /bin/bash, and creates a home directory.

Using these commands with the -r flag is useful when creating system users in Docker containers. Let me know if you have any other questions!

在Dockerfile中切换到非root用户来执行程序,需要注意以下几点:

## 定义非root用户

使用`USER`指令来指定运行镜像的用户。例如:
USER john   # 指定john用户 


该用户必须是镜像内已存在的用户。可以使用`RUN useradd`等命令创建用户。

## 设置正确的文件权限  

以非root用户运行前,需要确保文件和目录具有适当的权限。例如:

RUN chown -R john:john /some/directory  # 授予john用户目录访问权限 


## 考虑安全隐患

以非root用户运行可以减小安全风险,但不意味着绝对安全。仍需谨慎编写Dockerfile及应用程序代码。特别是避免提升权限到root。

## 完整例子

下面是一个简单的例子:
dockerfile
# 创建john用户 
RUN useradd john  

# 将/app目录的拥有者改为john  
RUN chown -R john:john /app 

# 将工作目录切换到/app 
WORKDIR /app  

# 以john用户运行容器 
USER john

# 运行python应用 
CMD [ "python", "./app.py" ] 


此镜像以john用户的权限运行python应用app.py。在构建和运行此镜像的过程中以及在最终容器内,都无法以root权限运行任何命令。这减小了安全风险,是Dockerfile的最佳实践。

