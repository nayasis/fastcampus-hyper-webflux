from locust import HttpUser, task, between, TaskSet


class UserBehavior(TaskSet):
    # @task
    # def getPost(self):
    #     postId = 1
    #     self.client.get(f'/post/{postId}')
    # @task
    # def getAllPost(self):
    #     userId = 1
    #     self.client.get(f'/post/all?title=st')
    @task
    def delay(self):
        userId = 1
        self.client.get(f'/stress/delay')


class LocustUser(HttpUser):
    host  = "http://localhost:8080"
    tasks = [ UserBehavior ]
    # wait_time = between(1, 3)