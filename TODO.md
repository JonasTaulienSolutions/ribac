# To Do
* Add `POST /groups`
* Add `GET /groups`
* Add `DELETE /groups/{groupName}`
* Add `POST /groups/{groupName}/members`
* Add `GET /groups/{groupName}/members`
* Add `DELETE /groups/{groupName}/members/{userId}`

* Add `POST /rights`
* Add `DELETE /rights/{rightName}`
* Add `GET /rights`

* Add `POST /users/{userId}/rights`
* Add `GET /users/{userId}/rights`
* Add `DELETE /users/{userId}/rights/{rightName}`

* Add `POST /groups/{groupName}/rights`
* Add `GET /groups/{groupName}/rights`
* Add `DELETE /groups/{groupName}/rights/{rightName}`

* Add `POST /right-sets`
* Add `DELETE /right-sets/{rightSetName}`
* Add `POST /right-sets/{rightSetName}/rights`
* Add `GET /right-sets/{rightSetName}/rights`
* Add `DELETE /right-sets/{rightSetName}/rights/{rightName}`
* Add `GET /right-sets`

* Add `POST /users/{userId}/right-sets`
* Add `GET /users/{userId}/right-sets`
* Add `DELETE /users/{userId}/right-sets/{rightSetName}`

* Add `POST /groups/{groupName}/right-sets`
* Add `GET /groups/{groupName}/right-sets`
* Add `DELETE /groups/{groupName}/right-sets/{rightSetName}`

* Add `GET /users/{userId}/has-right`
* Add `GET /users/{userId}/has-multiple-rights`
    * Request
        ```json
        [
          "Right 1",
          "Right 2",
          "Right 3"
        ]
        ```
    * Response
        ```json
        {
            "has-all": false,
            "has-any": true,
            "results": {
                "Right 1": false,
                "Right 2": true,
                "Right 3": false
            }
        }
        ```
* Add `GET /users/{userId}/all-rights`
* Add `GET /group/{groupId}/all-rights`

* Finish tests in `UserFunctionalTest`
    * deleteUser_alsoDeletesEveryGroupMembership
    * deleteUser_alsoDeletesEveryUserRight
    * deleteUser_alsoDeletesEveryUserRights
* Test sending non-json as body
* Introduce 'createdAt' fields
* Introduce Flyway
* Implement java client
* Implement transactions