from locust import FastHttpUser, task, between, TaskSet
from locust import events
from locust.runners import MasterRunner, WorkerRunner

class UserBehavior(TaskSet):
    # @task
    # def get(self):
    #     self.client.get(f'/article/1')
    @task
    def getAll(self):
        self.client.get(f'/article/all?title=matched')
    # @task
    # def delay(self):
    #     self.client.get(f'/stress/delay')


class LocustUser(FastHttpUser):
    host     = "http://10.202.96.73:8080"
    tasks    = [ UserBehavior ]
    min_wait = 5000
    max_wait = 15000
