
## How Can I Contribute?

The issues have been integrated into all stages of the development process. This way, the work is coordinated through the so-called Agile Management following Scrum techniques. For this process, we used Projects in GitHub.

First, you should create a new issue with the bug or new behaviour that you want to implement in 9 Cards. You also can contribute implementing the [existing issues in 9 Cards](https://github.com/47deg/nine-cards-v2/issues)  

When you create a new issue you have to add [the labels](https://github.com/47deg/nine-cards-v2/labels) in order to other developers understand the problem or new behaviour

The mandatory labels are:

- **Story Points:** Rate the relative effort of work in a Fibonacci-like format: 1, 2, 3, 5, or 8. If we are thinking on time the correspondence for every SP is: 2 hours, 1 day, 2 or 3 days, 1 week and 2 weeks. Maybe, if you want to put 8 SP on one issue, you should divide the issue
- **Server or Client:** You should add a new label if the the issue is for [server](https://github.com/47deg/nine-cards-backend) or [client](https://github.com/47deg/nine-cards-v2). In addition, if it's a client issue, you can add a `ui` label if you only is working in UI
- **Expertise Level:** Add the label for `beginner`, `intermediate` or `advance`
 
You have more labels that you can use if you think that it's interesting for other developers as `bug`, `critival`, `test` and so on
 
When you have selected the issue that you want to work, you must add the issue in [the board](https://github.com/47deg/nine-cards-v2/projects) (Server or Client) in `In progess` column. After that, you should create a new `branch` where you'll implement the code. The name of the branch is important:

- [Github Name]-[Issue Number]-[Small Description]

For example, `47dev-1213-Fixing_Tests`

Every issue passes through four statuses:

- **Development:** you are resolving the issue. The issue is in `In Progress` column
- **Code review:** other person is reviewing the style of the code. You can assign the issue to other developer. The issue is in `Code review` column. You need a `LGTM!` or `Thumb up` for passing to next step
- **QA:** other person verifies that the code resolves the issue. The issue is in `QA` column. If the branch covers the description as expected, you can pass to the other step
- **Ready to Master:** The issue is in `Ready to Master` column. You have to wait the we include the code in master

If you finish all process, you'll be a contributor of 9 Cards and we'll be happy for that!
