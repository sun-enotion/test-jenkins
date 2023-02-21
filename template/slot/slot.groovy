def kSlotService() {
    def  SLOT_SERVICE = """
        apiVersion: apps/v1
        kind: Deployment
        metadata:
          creationTimestamp: null
          labels:
            app: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
          name: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
          namespace: ${DEPLOYMENT_NAME}
        spec:
          replicas: ${REPLICAS_NUMBER}
          selector:
            matchLabels:
              app: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
          strategy: {}
          template:
            metadata:
              labels:
                app: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
            spec:
              containers:
              - name: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
                image: ${DOCKERHUB}/${DEPLOYMENT_NAME}-${DEPLOY_ENV}:${REPLICAS_NUMBER}
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
            app: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
          name: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
          namespace: ${DEPLOYMENT_NAME}
        spec:
          ports:
          - name: bo-auth-http
            port: ${SERVICE_PORT}
            protocol: TCP
            targetPort: ${SERVICE_PORT}
          selector:
            app: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
          type: ClusterIP
        status:
          loadBalancer: {}

    """.stripIndent()
    writeFile(file: "${DEPLOYMENT_NAME}-${DEPLOY_ENV}.yml", text: SLOT_SERVICE)
}

return this