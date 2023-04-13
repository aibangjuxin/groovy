Groovy 可以通过 GitHub API 获取 pull request 的评论信息和审核信息。在获取这些信息后，可以使用 Groovy 代码来解析它们以查找某个组的成员是否进行了评论或审核。
以下是一些示例代码，可以用来获取 pull request 的评论和审核信息，并检查是否来自特定组的成员：
获取 pull request 的评论信息：
import groovy.json.JsonSlurper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

def getPullRequestComments(String owner, String repo, Integer pullRequestNumber) {
    def url = "https://api.github.com/repos/${owner}/${repo}/pulls/${pullRequestNumber}/comments"
    def client = HttpClientBuilder.create().build()
    def request = new HttpGet(url)
    request.setHeader("Authorization", "Bearer <token>")
    def response = client.execute(request)
    def json = new JsonSlurper().parseText(EntityUtils.toString(response.entity))
    return json
}

def comments = getPullRequestComments("owner", "repo", 123)
comments.each { comment ->
    def user = comment.user.login
    def body = comment.body
    def groupMembers = ["group_member_1", "group_member_2", "group_member_3"]
    if (groupMembers.contains(user)) {
        println("The comment '${body}' was made by a member of the group.")
    }
}

获取 pull request 的审核信息
import groovy.json.JsonSlurper
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

def getPullRequestReviews(String owner, String repo, Integer pullRequestNumber) {
    def url = "https://api.github.com/repos/${owner}/${repo}/pulls/${pullRequestNumber}/reviews"
    def client = HttpClientBuilder.create().build()
    def request = new HttpGet(url)
    request.setHeader("Authorization", "Bearer <token>")
    def response = client.execute(request)
    def json = new JsonSlurper().parseText(EntityUtils.toString(response.entity))
    return json
}

def reviews = getPullRequestReviews("owner", "repo", 123)
reviews.each { review ->
    def user = review.user.login
    def state = review.state
    def groupMembers = ["group_member_1", "group_member_2", "group_member_3"]
    if (groupMembers.contains(user)) {
        println("The review is '${state}' and was made by a member of the group.")
    }
}

在这些示例中，您需要替换 <token>, owner, repo, pullRequestNumber 和 group_member_X，以便与您自己的 GitHub 帐户和存储库匹配。请注意，您需要提供一个有效的 GitHub 访问令牌以进行 API 调用。

使用groovy 对获取的review.json文件分析 是否某个组的成员进行了评论或者审核

可以使用Groovy语言读取review.json文件并解析其中的内容，判断是否某个组的成员进行了评论或审核。以下是一个示例代码：
import groovy.json.JsonSlurper

// 读取review.json文件并解析成JSON对象
def jsonSlurper = new JsonSlurper()
def reviewJson = jsonSlurper.parseText(new File("review.json").text)

// 获取评论或审核人列表
def reviewers = []
reviewJson.each { review ->
    if (review.state == "COMMENTED" || review.state == "APPROVED") {
        reviewers << review.user.login
    }
}

// 判断某个组的成员是否进行了评论或审核
def groupName = "example-group"
def isGroupReviewer = reviewers.any { reviewer ->
    reviewer in groupName
}

// 输出结果
println "Group $groupName has " + (isGroupReviewer ? "" : "not ") + "reviewed or commented on this review."

在这个示例代码中，我们使用Groovy语言中的JsonSlurper类读取review.json文件，并将其解析成JSON对象。然后，我们遍历每个审核记录，将评论或审核的人添加到一个名为reviewers的列表中。接下来，我们判断列表中是否存在来自指定组的成员，
如果存在则表示该组的成员进行了评论或审核，否则表示没有进行评论或审核。最后，我们输出结果


groovy 分析获取的github review json文件,然后判断approve或者commont的用户是不是定义的特殊组内的成员

To analyze the GitHub review JSON file and determine if the approving or commenting user is a member of a specific group, you can use the following steps:

1. Parse the JSON file: Use a JSON parser to parse the review JSON file and extract the relevant information, such as the approving and commenting users.

2. Check if the user is a member of the group: Use the GitHub API to check if the user is a member of the specific group. You can do this by sending a GET request to the following endpoint: https://api.github.com/orgs/{org}/teams/{team_slug}/memberships/{username}. Replace {org} with the name of your organization, {team_slug} with the slug of the team you want to check membership for, and {username} with the username of the user you want to check.

3. Handle the response: The response from the API will contain a state field, which can be active, pending, or inactive. If the user is a member of the group, the state will be active. If the user is not a member of the group, the state will be pending or inactive.

Here's an example of how you can implement this in Groovy:
import groovy.json.JsonSlurper
import java.net.URL

def json = new JsonSlurper().parseText(reviewJson)

def approvingUser = json.pull_request.user.login
def commentingUser = json.user.login

def isApprovingUserInGroup = isUserInGroup(approvingUser, "my-group")
def isCommentingUserInGroup = isUserInGroup(commentingUser, "my-group")

def isUserInGroup(username, groupSlug) {
    def url = "https://api.github.com/orgs/my-org/teams/${groupSlug}/memberships/${username}"
    def connection = new URL(url).openConnection()
    connection.setRequestProperty("Authorization", "Bearer ${accessToken}")
    def response = new JsonSlurper().parse(connection.getInputStream())
    return response.state == "active"
}

Replace reviewJson with the JSON string you want to parse, my-group with the slug of the group you want to check membership for, and accessToken with your GitHub access token.

可以使用Groovy的JSON解析器来解析GitHub Review JSON文件，并使用Groovy的集合和条件语句来判断审批或评论的用户是否属于特定组。

以下是一个示例代码，它假设你已经从GitHub API获取了一个Review JSON文件，并将其存储在一个名为`reviewJson`的字符串变量中。它还假设你已经定义了一个名为`specialGroup`的字符串列表，其中包含特殊组的成员。

```
import groovy.json.JsonSlurper

// 解析JSON文件
def review = new JsonSlurper().parseText(reviewJson)

// 获取审批和评论
def approvals = review.events.findAll { it.event == 'APPROVE' }
def comments = review.events.findAll { it.event == 'COMMENT' }

// 获取所有审批和评论的用户
def users = (approvals + comments).collect { it.user.login }

// 检查特殊组的成员是否在用户列表中
def isSpecialGroupMember = users.any { specialGroup.contains(it) }

// 打印结果
if (isSpecialGroupMember) {
    println "At least one member of the special group has approved or commented on this review"
} else {
    println "No members of the special group have approved or commented on this review"
}
```

这个代码首先使用`JsonSlurper`解析JSON文件，并使用`findAll`方法获取所有的审批和评论。然后，它使用`collect`方法获取所有审批和评论的用户，并使用`any`方法检查特殊组的成员是否在用户列表中。最后，它打印结果。