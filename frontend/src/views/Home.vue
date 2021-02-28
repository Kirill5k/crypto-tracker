<template>
  <b-container>
    <date-range-picker
      ref="dateRangePicker"
      @show="getMentions"
    />
  </b-container>
</template>

<script>
import { BContainer } from 'bootstrap-vue'
import DateRangePicker from '@/components/DateRangePicker.vue'

export default {
  name: 'Home',
  components: { BContainer, DateRangePicker },
  mounted () {
    this.$refs.dateRangePicker.show()
  },
  methods: {
    getMentions (dates) {
      this.$store.dispatch('getMentions', dates)
    }
  }
}
</script>
