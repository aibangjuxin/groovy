

# 仅克隆某个分支下的某个目录
使用Git的sparse-checkout功能来仅克隆某个分支下的某个目录。这可以通过以下步骤完成：

1. **克隆仓库：**
   克隆整个仓库，但在克隆时指定`--no-checkout`选项：

   ```bash
   git clone --no-checkout <repository_url>
   ```
`git clone --no-checkout <repository_url>` 
是Git克隆命令的一种形式，其中的 `--no-checkout` 选项的作用是告诉Git在克隆仓库时不要立即检出（checkout）任何分支。这样可以加快克隆速度，因为不会将整个工作目录的文件都拉取下来。

具体来说，该命令的步骤是：

1.1 **克隆仓库：**
   执行 `git clone --no-checkout <repository_url>` 命令会将仓库的元数据（例如.git目录）和历史记录下载到本地，但不会在工作目录中创建实际文件。

1.2. **不检出分支：**
   由于使用了 `--no-checkout` 选项，Git不会自动检出任何分支，这意味着工作目录中的文件还没有被填充。

1.3. **手动切换分支：**
   在接下来的步骤中，您可以手动切换到所需的分支，例如：

   ```bash
   git switch <branch_name>
   ```

   或者，如果您使用的是较旧版本的Git：

   ```bash
   git checkout <branch_name>
   ```

通过延迟检出分支，您可以选择性地检出仓库中的特定分支或目录，这对于只需要部分仓库内容的情况很有用。 


2. **切换到所需的分支：**
   进入克隆的仓库目录并切换到所需的分支，例如：

   ```bash
   cd <repository_directory>
   git switch <branch_name>
   ```

   或者，如果您使用的是较旧版本的Git，可以使用以下命令：

   ```bash
   git checkout <branch_name>
   ```

3. **启用sparse-checkout：**
   启用sparse-checkout，并指定要包含的目录：

   ```bash
   git sparse-checkout init --cone
   git sparse-checkout set <directory_path>
   ```

   请将 `<directory_path>` 替换为您要包含的目录的相对路径。

4. **拉取仓库内容：**
   拉取仓库的内容：

   ```bash
   git pull origin <branch_name>
   ```

现在，您只克隆了指定分支下的指定目录。请注意，这仅适用于较新版本的Git（2.25及以上）。如果您的Git版本较旧，请考虑升级到最新版本。

- My story history
```
git clone --no-checkout <repository_url>
cd <repository_directory>
git switch <branch_name>
git sparse-checkout init --cone
git sparse-checkout set <directory_path>
git pull origin <branch_name>

```

5. **进行你的修改：**
   1. eg: vi Release-git.md
6. **提交你的修改：**
   1. git add <modified_files>
   2. git commit -m "release-git"
7. ** push to Remote branch **
   1. git push origin <branch_name>


请注意，由于您是从指定目录克隆的，只有该目录的修改会被跟踪和提交。其他未包含在 sparse-checkout 中的文件不会被Git感知。

- My Testing history
```
pwd
/Users/lex/temp
git clone --no-checkout git@github.com:aibangjuxin/groovy.git

git branch -a
* main
  remotes/origin/HEAD -> origin/main
  remotes/origin/Lex-Release
  remotes/origin/main
(END)

git checkout Lex-Release
branch 'Lex-Release' set up to track 'origin/Lex-Release'.
Switched to a new branch 'Lex-Release'

lex@Lexs-MacBook-Pro  ~/temp/groovy   Lex-Release 
 ✔  3689  09:51:38
pwd
/Users/lex/temp/groovy
 lex@Lexs-MacBook-Pro  ~/temp/groovy   Lex-Release 
 ✔  3690  09:52:19
git sparse-checkout init --cone
 lex@Lexs-MacBook-Pro  ~/temp/groovy   Lex-Release ● 
 ✔  3691  09:52:21
git sparse-checkout set howgit
 lex@Lexs-MacBook-Pro  ~/temp/groovy   Lex-Release ● 
 ✔  3692  09:52:51
ls howgit/
Release-git.md         fork.md                master-conver-local.md

查看目录下文件
pwd
/Users/lex/temp/groovy
 lex@Lexs-MacBook-Pro  ~/temp/groovy   Lex-Release ● 
 ✔  3701  09:56:17
仅有几个而已
ls
Readme.md   git.sh      howgit      markdown.md rais.sh

修改文件
 lex@Lexs-MacBook-Pro  ~/temp/groovy/howgit   Lex-Release ● 
 ✔  3707  09:59:16
vi Release-git.md
 lex@Lexs-MacBook-Pro  ~/temp/groovy/howgit   Lex-Release ● 
 ✔  3708  09:59:39

提交修改
git add .
 lex@Lexs-MacBook-Pro  ~/temp/groovy/howgit   Lex-Release ✚ ● 
 ✔  3709  10:00:49
git commit -a -m "edit Lex-Release file and commit"
[Lex-Release 34cae4e] edit Lex-Release file and commit
 1 file changed, 2 insertions(+), 2 deletions(-)

推送到远程仓库
git push origin <branch_name> OR git push

```