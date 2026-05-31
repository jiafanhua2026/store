import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

let stompClient = null

export function connect(callback) {
  const socket = new SockJS('/ws')
  stompClient = Stomp.over(socket)
  
  stompClient.connect({}, () => {
    stompClient.subscribe('/topic/orders', (message) => {
      const order = JSON.parse(message.body)
      callback('order', order)
    })
    
    stompClient.subscribe('/topic/stats', (message) => {
      const stats = JSON.parse(message.body)
      callback('stats', stats)
    })
    
    stompClient.subscribe('/topic/stock', (message) => {
      const stock = JSON.parse(message.body)
      callback('stock', stock)
    })
  }, (error) => {
    console.error('WebSocket connection error:', error)
    setTimeout(() => connect(callback), 5000)
  })
}

export function disconnect() {
  if (stompClient) {
    stompClient.disconnect()
  }
}

export function sendNewOrder(order) {
  if (stompClient) {
    stompClient.send('/app/newOrder', {}, JSON.stringify(order))
  }
}

export function sendStatsUpdate(stats) {
  if (stompClient) {
    stompClient.send('/app/updateStats', {}, JSON.stringify(stats))
  }
}