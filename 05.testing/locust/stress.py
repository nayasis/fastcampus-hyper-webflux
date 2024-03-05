from locust import FastHttpUser, task, between, TaskSet
from locust import events
from locust.runners import MasterRunner, WorkerRunner

class MyTask(TaskSet):
    # wait_time = between(2,5)
    @task(100)
    def get(self):
        self.client.get(f'/article/2')
    @task(1)
    def getAll(self):
        self.client.get(f'/article/all?title=matched')
    # @task
    # def delay(self):
    #     self.client.get(f'/stress/delay')

class LocustUser(FastHttpUser):
    host = "http://localhost:8080"
    tasks = [ MyTask ]
    min_wait = 5000
    max_wait = 15000
