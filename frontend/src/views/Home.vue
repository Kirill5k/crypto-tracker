<template>
  <b-container>
    <date-range-picker
      ref="dateRangePicker"
      @show="getMentions"
    />
    <mentions-chart
      v-if="mentions.length"
      :mentions="mentions"
    />
  </b-container>
</template>

<script>
import { BContainer } from 'bootstrap-vue'
import DateRangePicker from '@/components/DateRangePicker.vue'
import MentionsChart from '@/components/MentionsChart.vue'

export default {
  name: 'Home',
  components: { BContainer, DateRangePicker, MentionsChart },
  mounted () {
    this.$refs.dateRangePicker.show()
  },
  computed: {
    mentions () {
      return this.$store.state.mentions
    }
  },
  methods: {
    getMentions (dates) {
      this.$store.dispatch('getMentions', dates)
    }
  }
}
</script>
