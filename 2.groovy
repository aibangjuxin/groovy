您可以使用 `git cherry-pick` 命令来将某个分支上的特定 commit 合并到 master 分支上。具体操作步骤如下：

1. 切换到 master 分支：`git checkout master`
2. 执行 cherry-pick 命令，
将需要合并的 commit ID 传入：`git cherry-pick <commit ID>`
3. 如果需要合并多个 commit，
可以重复执行 cherry-pick 命令
4. 合并完成后，将修改推送到远程仓库：
`git push origin master`

请注意，使用 cherry-pick 命令合并 commit 时，
可能会导致代码冲突。如果出现冲突，请根据提示进行手动解决。

您可以使用以下命令来查看 commit ID：

```
git log
```

执行该命令后，会显示当前分支的所有 commit 记录，
每个 commit 记录都包含一个唯一的 commit ID。
您可以使用上下箭头来查看不同的 commit 记录，
commit ID 通常显示在每个记录的开头，如下所示：

```
commit 4b5d6e1f8f9b1b2e4f2a5c7b3d4e5f6a7b8c9d0e
Author: John Doe <johndoe@example.com>
Date:   Tue May 4 18:00:00 2023 +0800

    Add new feature
```

在上面的示例中，commit ID 为 `4b5d6e1f8f9b1b2e4f2a5c7b3d4e5f6a7b8c9d0e`。您可以将其复制并在其他命令中使用。