package kirill5k.cryptotracker.clients.reddit

import kirill5k.cryptotracker.clients.reddit.responses.Submission
import kirill5k.cryptotracker.domain.Mention

private[reddit] object mappers {

  object MentionsMapper {
    def map(submission: Submission): List[Mention] = ???
  }
}
