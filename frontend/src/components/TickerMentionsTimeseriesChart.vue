<template>
  <div class="ticker-mentions-timeseries-chart">
    <line-chart
      :ticker="ticker"
      :chart-data="chartData"
      :options="options"
    />
  </div>
</template>

<script>
import LineChart from './charts/LineChart.js'

const DEFAULT_OPTIONS = {
  responsive: true,
  legend: {
    display: true
  },
  maintainAspectRatio: false,
  scales: {
    x: {
      type: 'time',
      time: {
        unit: 'hour',
        displayFormats: {
          quarter: 'HH, MM'
        }
      }
    }
  }
}

export default {
  name: 'TickerMentionsTimeseriesChart',
  components: { LineChart },
  props: {
    ticker: {
      type: String,
      required: true
    },
    mentionTimes: {
      type: Array,
      required: true
    }
  },
  computed: {
    options () {
      return {
        ...DEFAULT_OPTIONS
      }
    },
    chartData () {
      const labels = []
      const datasets = [{
        label: this.ticker,
        backgroundColor: '#03c2fc',
        data: this.mentionTimes.map(t => Date.parse(t).getTime())
      }]
      return { labels, datasets }
    }
  }
}
</script>

<style scoped lang="scss">

</style>
