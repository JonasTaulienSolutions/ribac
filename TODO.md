# To Do
## Groups
* Add `DELETE /groups/{groupName}`
* Add `GET /groups/{groupName}`

## Memberships
* Add `POST /groups/{groupName}/members`
* Add `GET /groups/{groupName}/members`
* Add `DELETE /groups/{groupName}/members/{userId}`
* Finish test `UserFunctionalTest::deleteUser_alsoDeletesEveryGroupMembership`

## Rights
* Add `POST /rights`
* Add `DELETE /rights/{rightName}`
* Add `GET /rights/{rightName}`
* Add `GET /rights`

## User Rights
* Add `POST /users/{userId}/rights`
* Add `GET /users/{userId}/rights` (Direct rights)
* Add `DELETE /users/{userId}/rights/{rightName}`
* Add `GET /users/{userId}/all-rights` (Direct and indirect rights)
* Finish test `UserFunctionalTest::deleteUser_alsoDeletesEveryUserRight`

## Group Rights
* Add `POST /groups/{groupName}/rights`
* Add `GET /groups/{groupName}/rights` (Direct rights)
* Add `DELETE /groups/{groupName}/rights/{rightName}`
* Add `GET /group/{groupId}/all-rights` (Direct and indirect rights)

## Right Sets
* Add `POST /right-sets`
* Add `DELETE /right-sets/{rightSetName}`
* Add `GET /right-sets/{rightSetName}`

## Right Set Items
* Add `POST /right-sets/{rightSetName}/rights`
* Add `GET /right-sets/{rightSetName}/rights`
* Add `DELETE /right-sets/{rightSetName}/rights/{rightName}`
* Add `GET /right-sets`

## User Right Sets
* Add `POST /users/{userId}/right-sets`
* Add `GET /users/{userId}/right-sets`
* Add `DELETE /users/{userId}/right-sets/{rightSetName}`
* Finish test `UserFunctionalTest::deleteUser_alsoDeletesEveryUserRightSet`

## Group Right Sets
* Add `POST /groups/{groupName}/right-sets`
* Add `GET /groups/{groupName}/right-sets`
* Add `DELETE /groups/{groupName}/right-sets/{rightSetName}`

## Has User Right
* Add `GET /users/{userId}/has-right/{rightName}` (Has direct or indirect right?)
* Add `GET /users/{userId}/has-multiple-rights` (Has all/any direct or indirect rights?)
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

* Test sending non-json as body
* Introduce 'createdAt' fields
* Introduce Flyway
* Implement java client
* Implement transactions
* Rename things