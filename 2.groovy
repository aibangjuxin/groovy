您可以使用 `git cherry-pick` 命令来将某个分支上的特定 commit 合并到 master 分支上。具体操作步骤如下：

1. 切换到 master 分支：`git checkout master`
2. 执行 cherry-pick 命令，将需要合并的 commit ID 传入：`git cherry-pick <commit ID>`
3. 如果需要合并多个 commit，可以重复执行 cherry-pick 命令
4. 合并完成后，将修改推送到远程仓库：`git push origin master`

请注意，使用 cherry-pick 命令合并 commit 时，可能会导致代码冲突。如果出现冲突，请根据提示进行手动解决。