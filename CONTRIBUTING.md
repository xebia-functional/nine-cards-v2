
## How Can I Contribute?

The issues have been integrated into all stages of the development process. This way, the work is coordinated through the so-called Agile Management following Scrum techniques. For this process, we used Projects in GitHub.

First, you should create a new issue with the bug or new behaviour that you want to implement in 9 Cards. You also can contribute implementing the [existing issues in 9 Cards](https://github.com/47deg/nine-cards-v2/issues).  

When you create a new issue you have to add [the labels](https://github.com/47deg/nine-cards-v2/labels) so other developers can understand the problem or new behaviour.

The mandatory labels are:

- **Story Points:** Rate the relative work effort in a Fibonacci-like format: 1, 2, 3, 5, or 8. If we are referring to time, the correspondence for every SP is: 2 hours, 1 day, 2 or 3 days, 1 week and 2 weeks. If you want to put 8 SP on one issue, you should consider dividing the issue.
- **Server or Client:** You should add a new label if the issue is for the [server](https://github.com/47deg/nine-cards-backend) or [client](https://github.com/47deg/nine-cards-v2). In addition, if it's a client issue, you can add a `ui` label if you're only working on UI.
- **Expertise Level:** Add the label for `beginner`, `intermediate` or `advanced`.
 
You have more labels that you can use if you think they're relevent for other developers such as `bug`, `critical`, `test`, and so on.
 
When you have selected the issue that you want to work on, you must add the issue in [the board](https://github.com/47deg/nine-cards-v2/projects) (Server or Client) in the `In progess` column. After that, you should create a new `branch` where you'll implement the code. The name of the branch is important:

- [Github Name]-[Issue Number]-[Small Description]

For example, `47dev-1213-Fixing_Tests`

Every issue passes through four statuses:

- **Development:** you are resolving the issue. The issue is in `In Progress` column.
- **Code review:** another person is reviewing the style of the code. You can assign the issue to another developer. The issue is in the `Code review` column. You need a `LGTM!` or `Thumbs up` to pass on to the next step.
- **QA:** another person verifies that the code resolves the issue. The issue is in `QA` column. If the branch covers the description as expected, you can pass on to the next step.
- **Ready to Master:** The issue is in the `Ready to Master` column. You have to wait until we include the code in master.

If you finish the process, you'll be a contributor of 9 Cards and we'll be happy to have you!
