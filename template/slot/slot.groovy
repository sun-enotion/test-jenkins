def kSlotService() {
    def  SLOT_SERVICE = """
        apiVersion: apps/v1
        kind: Deployment
        metadata:
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
          labels:
            app: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
          name: ${DEPLOYMENT_NAME}-${DEPLOY_ENV}
          namespace: ${DEPLOYMENT_NAME}
        spec:
          ports:
          - name: tcp-port
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

def systemdSlotService () {
  def SLOT_SERVICE = """
      [Unit]
      Description=Manage ${GAMES_NAME} service
      After=network.target remote-fs.target nss-lookup.target

      [Service]
      WorkingDirectory=/var/games/${GAMES_NAME}
      ExecStart=/usr/bin/java -jar -XX:+UseG1GC -Xmx1g -Dspring.profiles.active=game,test ${CURRENT_FILES_NAME}.jar --spring.config.name=application-game,application-test  --logging.path=/var/www/log
      User=root
      Type=simple
      LimitNOFILE=infinity
      LimitNPROC=infinity
      LimitFSIZE=infinity
      LimitCPU=infinity
      LimitAS=infinity
      LimitRSS=infinity
      LimitCORE=infinity
      #Restart=on-failure
      #RestartSec=10

      [Install]
      WantedBy=multi-user.target
  """.stripIndent()
  writeFile(file: "${GAMES_NAME}.service", text: SLOT_SERVICE)
}

return this