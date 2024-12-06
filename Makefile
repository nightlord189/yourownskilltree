build:
	docker build -t yourownskilltree .

run:
	docker run yourownskilltree

run-all:
	docker-compose up -d

run-rabbit:
	docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
