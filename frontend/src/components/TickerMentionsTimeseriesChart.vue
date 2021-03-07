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

const addZero = num => num.toString().length === 1 ? `0${num}` : num.toString()

const DEFAULT_OPTIONS = {
  responsive: true,
  legend: {
    display: true
  },
  maintainAspectRatio: false
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
    },
    dateFrom: {
      type: String,
      required: true
    },
    dateTo: {
      type: String,
      required: true
    }
  },
  computed: {
    options () {
      return {
        ...DEFAULT_OPTIONS,
        onClick: (evt, item) => {
          if (item && item.length) {
            const x = this.chartData.labels[item[0]._index]
            const hour = x.split(',')[0]
            const day = x.split(' ')[1].split('/')[0]
            const month = x.split('/')[1]
            const clickedDateFrom = `${this.dateFrom.slice(0, 4)}-${month}-${day}T${hour}:${this.dateFrom.slice(14)}`
            const clickedDateTo = `${clickedDateFrom.slice(0, 13)}:${this.dateTo.slice(14)}`
            this.$emit('click', { dateFrom: clickedDateFrom, dateTo: clickedDateTo, ticker: this.ticker })
          }
        }
      }
    },
    chartData () {
      const mentionsByHour = this.mentionsCountedByHour
      const labels = this.dates.map(d => `${addZero(d.getHours())}, ${addZero(d.getDate())}/${addZero(d.getMonth() + 1)}`)
      const datasets = [{
        label: `${this.ticker} mentions`,
        borderWidth: 1,
        borderColor: '#3be38f',
        pointBackgroundColor: '#49c989',
        pointBorderColor: '#08361f',
        backgroundColor: '#3be38f',
        data: this.dates.map(d => d.toISOString().slice(0, 13)).map(d => mentionsByHour[d] || 0)
      }]
      return { labels, datasets }
    },
    mentionsCountedByHour () {
      return this.mentionTimes
        .map(t => new Date(t))
        .reduce((acc, el) => {
          const key = el.toISOString().slice(0, 13)
          acc[key] = !acc[key] ? 1 : acc[key] + 1
          return acc
        }, {})
    },
    dates () {
      const dates = []
      for (let d = new Date(this.dateFrom); d <= new Date(this.dateTo); d.setHours(d.getHours() + 1)) {
        dates.push(new Date(d))
      }
      return dates
    }
  }
}
</script>

<style scoped lang="scss">

</style>
