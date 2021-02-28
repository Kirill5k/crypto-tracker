<template>
  <div class="mentions-chart">
    <bar-chart
      :chart-data="chartData"
      :options="options"
    />
  </div>
</template>

<script>
import BarChart from './charts/BarChart.js'

export default {
  name: 'MentionsChart',
  components: { BarChart },
  props: {
    mentions: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      options: { responsive: true, maintainAspectRatio: false }
    }
  },
  computed: {
    chartData () {
      const labels = this.mentions.map(m => m.ticker)
      const datasets = [{ label: 'Mentions', backgroundColor: '#03c2fc', data: this.mentions.map(m => m.total) }]
      console.log(labels, datasets)
      return { labels, datasets }
    }
  }
}
</script>

<style scoped lang="scss">

</style>
