def kSlotService() {
    def  SLOT_SERVICE = """
        apiVersion: apps/v1
        kind: Deployment
        metadata:
          creationTimestamp: null
          labels:
            app: game-slot-${DEPLOYMENT_NAME}
          name: game-slot-${DEPLOYMENT_NAME}
          namespace: ${DEPLOYMENT_NAME}
        spec:
          replicas: ${REPLICAS}
          selector:
            matchLabels:
              app: game-slot-${DEPLOYMENT_NAME}
          strategy: {}
          template:
            metadata:
              labels:
                app: game-slot-${DEPLOYMENT_NAME}
            spec:
              containers:
              - name: game-slot-${DEPLOYMENT_NAME}
                image: ${DOCKERHUB}/game-slot-${DEPLOYMENT_NAME}:${GIT_TAG}
                ports:
                - containerPort: ${SERVICE_PORT}
                livenessProbe:
                  tcpSocket:
                    port: ${SERVICE_PORT}
                  initialDelaySeconds: 10
                  periodSeconds: 5
                readinessProbe:
                  httpGet:
                    path: /health
                    port: ${SERVICE_PORT}
                  initialDelaySeconds: 10
                  periodSeconds: 5
                resources: {}
              restartPolicy: Always
              nodeSelector:
                worker: bo-dev
        ---
        apiVersion: v1
        kind: Service
        metadata:
          creationTimestamp: null
          labels:
            app: game-slot-${DEPLOYMENT_NAME}
          name: game-slot-${DEPLOYMENT_NAME}
          namespace: ${DEPLOYMENT_NAME}
        spec:
          ports:
          - name: bo-auth-http
            port: ${SERVICE_PORT}
            protocol: TCP
            targetPort: ${SERVICE_PORT}
          selector:
            app: game-slot-${DEPLOYMENT_NAME}
          type: ClusterIP
        status:
          loadBalancer: {}

    """.stripIndent()
    writeFile(file: "game-slot-${DEPLOYMENT_NAME}.yml", text: SLOT_SERVICE)
}

return this