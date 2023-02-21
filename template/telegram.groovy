import java.text.SimpleDateFormat

def message(groups, message, showTime) {
  def groupArr = groups.split(",")

  def groupMap = [
    // BE_CICD: '-867854695',
    // FE_CICD: '-123456'
    BE_CICD: '694673044'
  ]

  if (showTime) {
    def date = new Date()
    TimeZone hcm = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    def timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    timeFormat.setTimeZone(hcm)
    message = message + "\nBuild: ${timeFormat.format(date)}"
  }

  message = URLEncoder.encode(message, "UTF-8")

  groupArr.each{ group ->
    def groupName = group.trim()

    httpRequest "https://api.telegram.org/bot6184040607:AAGeWN1F-Aj43i8Kz-h0vRF4GNTh8-Hyy9k/sendmessage?chat_id=${groupMap[groupName]}&text=${message}"
  }
}

return this
