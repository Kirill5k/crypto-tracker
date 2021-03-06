import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const reject = (res) => res.json().then(e => Promise.reject(new Error(e.message)))

export default new Vuex.Store({
  state: {
    isLoading: false,
    tickerMentions: [],
    dateFrom: null,
    dateTo: null,
    mentionsSummaries: []
  },
  mutations: {
    setMentionsSummaries (state, mentions) {
      state.mentionsSummaries = mentions.summaries.slice(0, 20)
      state.dateFrom = mentions.dateRange.from
      state.dateTo = mentions.dateRange.to
    },
    setTickerMentions (state, tickerMentions) {
      state.tickerMentions = tickerMentions
    },
    loading (state) {
      state.isLoading = true
    },
    loaded (state) {
      state.isLoading = false
    }
  },
  actions: {
    getMentionsSummaries ({ commit, state }, { dateFrom, dateTo }) {
      return fetch(`/api/mentions/summary?from=${dateFrom}&to=${dateTo}`)
        .then(res => {
          commit('loaded')
          return res.status === 200 ? res.json() : reject(res)
        })
        .then(res => commit('setMentionsSummaries', res))
    },
    getTickerMentions ({ commit, state }, { ticker, dateFrom, dateTo }) {
      return fetch(`/api/mentions/${ticker}?from=${dateFrom}&to=${dateTo}`)
        .then(res => res.status === 200 ? res.json() : reject(res))
        .then(res => commit('setTickerMentions', res))
    }
  },
  modules: {
  }
})
