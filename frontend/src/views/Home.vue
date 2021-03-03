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
      v-if="tickerMentionTimes.length"
      :ticker="selectedTicker"
      :mention-times="tickerMentionTimes"
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
    }
  },
  methods: {
    getMentions (dates) {
      this.$store.dispatch('getMentions', dates)
    },
    displayTickerMentionTimeseries (ticker) {
      console.log(ticker)
    }
  }
}
</script>
