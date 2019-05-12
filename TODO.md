# To Do
* Fix test failure:

   ```
   UserFunctionalTest.fetchUser_returnsRequestedUser:301 Unexpected status code. Response body: '<html><body><h1>Resource not found</h1></body></html>' ==> expected: <200> but was: <404>
   ```
* Log Db-Communication
* Log Responses
* Write README.md
* Finish all Tests and corresponding implementations in `UserFunctionalTest`
* Fix `./do-integration-test.sh` not finishing because of `3<`
* ?Remove `GET /users/{userId}`?
* Add `DELETE /users/{userId}`
* Add `POST /groups`
* Add `DELETE /groups/{groupName}`

* Add `POST /groups/{groupName}/members`
* Add `GET /groups/{groupName}/members`
* Add `DELETE /groups/{groupName}/members/{userId}`

* Add `POST /rights`
* Add `DELETE /rights/{rightName}`

* Add `POST /right-sets`
* Add `DELETE /right-sets/{rightSetName}`

* Add `POST /users/{userId}/rights`
* Add `GET /users/{userId}/rights`
* Add `DELETE /users/{userId}/rights/{rightName}`

* Add `POST /users/{userId}/right-sets`
* Add `GET /users/{userId}/right-sets`
* Add `DELETE /users/{userId}/right-sets/{rightSetName}`

* Add `POST /groups/{groupName}/rights`
* Add `GET /groups/{groupName}/rights`
* Add `DELETE /groups/{groupName}/rights/{rightName}`

* Add `POST /groups/{groupName}/right-sets`
* Add `GET /groups/{groupName}/right-sets`
* Add `DELETE /groups/{groupName}/right-sets/{rightSetName}`

* Add `GET /users/{userId}/has-right`
* Add `GET /users/{userId}/all-rights`