all: up

up:
	docker compose -f docker-compose.yml -p test up --build -d

down:
	docker compose -f docker-compose.yml -p test down

run:
	./gradlew bootRun

re: down up
