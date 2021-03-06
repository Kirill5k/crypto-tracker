<template>
  <b-container>
    <date-range-picker
      @show="getMentions"
    />
    <mentions-popularity-chart
      v-if="mentions.length"
      :mentions="mentions"
      @click="displayTickerMentionTimeseries"
    />
    <ticker-mentions-timeseries-chart
      v-if="tickerMentionTimes && tickerMentionTimes.length"
      :ticker="selectedTicker"
      :mention-times="tickerMentionTimes"
      :date-from="dateFrom"
      :date-to="dateTo"
      @click="displayTickerMentions"
    />
  </b-container>
</template>

<script>
import { BContainer } from 'bootstrap-vue'
import DateRangePicker from '@/components/DateRangePicker'
import MentionsPopularityChart from '@/components/MentionsPopularityChart'
import TickerMentionsTimeseriesChart from '@/components/TickerMentionsTimeseriesChart'

export default {
  name: 'Home',
  components: { BContainer, DateRangePicker, MentionsPopularityChart, TickerMentionsTimeseriesChart },
  data: () => ({
    selectedTicker: null,
    tickerMentionTimes: []
  }),
  computed: {
    mentions () {
      return this.$store.state.mentions
    },
    dateFrom () {
      return this.$store.state.dateFrom
    },
    dateTo () {
      return this.$store.state.dateTo
    }
  },
  methods: {
    getMentions (dates) {
      this.$store.dispatch('getMentions', dates).then(() => this.clearSelectedTicker())
    },
    displayTickerMentionTimeseries (ticker) {
      this.selectedTicker = ticker
      this.tickerMentionTimes = this.mentions.find(m => m.ticker === ticker)?.times
    },
    clearSelectedTicker () {
      this.tickerMentionTimes = []
      this.selectedTicker = null
    },
    displayTickerMentions (ticker) {
      console.log(ticker)
    }
  }
}
</script>
