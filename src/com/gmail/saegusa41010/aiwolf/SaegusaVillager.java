package com.gmail.saegusa41010.aiwolf;

import org.aiwolf.client.lib.Content;
import org.aiwolf.client.lib.DivinationContentBuilder;
import org.aiwolf.client.lib.EstimateContentBuilder;
import org.aiwolf.client.lib.RequestContentBuilder;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;

public class SaegusaVillager extends SaegusaBase {

	// 占い師カミングアウトが襲撃された際、残りの占い師カミングアウトが一人の場合は間違いなく黒
	// 占い師カミングアウトの意見が一致する場合は信用 wolfarrayからは消す
	// 占い結果が黒って出た人は50点
	// 占い結果が白って出た人は10下げ
	// 霊媒師が白ていってるのに黒って言ってる占い師は100点(霊媒一人なら100信じる)二人以上なら50

	// TODO Auto-generated constructor stub
	protected void chooseVoteCandidate() {
		werewolves.clear(); // 人狼リストをクリア
		/**
		 * for(Judge j : divinationList) { //占いリストを見ていく
		 * if(j.getResult()==Species.WEREWOLF && (j.getTarget() == me ||
		 * isKilled(j.getTarget()))) { Agent candidate = j.getAgent();
		 * if(isAlive(candidate) && !werewolves.contains(candidate)) {
		 * werewolves.add(candidate); } } }
		 */
		numMedium = 0;
		numSeer = 0;
		for (Agent agent : aliveOthers) {
			if (comingoutMap.get(agent) == Role.MEDIUM) {
				numMedium += 1;
			}
			if (comingoutMap.get(agent) == Role.SEER) {
				numSeer += 1;
			}
		}

		for (Judge j : divinationList) { // 占いリストを見ていく
			if (j.getResult() == Species.WEREWOLF && (j.getTarget() == me || isKilled(j.getTarget()))) {
				IdGuessList[j.getAgent().getAgentIdx()] += 100;
			}
			if (j.getResult() == Species.HUMAN) {
				IdGuessList[j.getTarget().getAgentIdx()] -= 10;
			}
			if (j.getResult() == Species.WEREWOLF) {
				IdGuessList[j.getTarget().getAgentIdx()] += 50;
			}
		}

		for (Judge j : identList) {
			if (j.getResult() == Species.HUMAN) {
				for(Judge j2 : divinationList) {
					if(j2.getResult() == Species.WEREWOLF ){
						if(numMedium ==1) {
							IdGuessList[j2.getAgent().getAgentIdx()] +=100;
						}
						else {
							IdGuessList[j2.getAgent().getAgentIdx()] += 50;
						}
					}

				}
			}
		}

		int maxscore = 0; // 一番ありえそうな推理を採用
		Guess maxGuess = null;

		for (Guess g : GuessList) { // 推理リストを順番に見ていき、点数をつける
			int score = 0; // guessのスコアを付けていく
			for (int w : g.getwolfarray()) {
				score += IdGuessList[w]; // 個人推理の結果を足していく
			}
			for (int p : g.getpossessedarray()) {
				score += IdGuessList[p]; // 個人推理の結果を足していく
			}
			g.setscore(score);
			if (score > maxscore) {
				maxscore = score;
				maxGuess = g;
			}
		}
		if (maxGuess != null) { // nullじゃなければwerewolvesリストを更新
			for (Integer i : maxGuess.getwolfarray()) {
				werewolves.add(AgentList.get(i));
			}
		}
		// 候補がいないとランダム
		if (werewolves.isEmpty()) {
			if (!aliveOthers.contains(voteCandidate)) {
				voteCandidate = randomSelect(aliveOthers);
			}
		} else {
			if (!werewolves.contains(voteCandidate)) {
				voteCandidate = randomSelect(werewolves);
				// 前回の投票と変わる場合
				if (canTalk) {
					talkQueue.offer(new Content(new EstimateContentBuilder(voteCandidate, Role.WEREWOLF)));
					talkQueue.offer(new Content(
							new RequestContentBuilder(null, new Content(new DivinationContentBuilder(voteCandidate)))));
				}
			}
		}
	}

	public String whisper() {
		throw new UnsupportedOperationException();
	}

	public Agent attack() {
		throw new UnsupportedOperationException();
	}

	public Agent divine() {
		throw new UnsupportedOperationException();
	}

	public Agent guard() {
		throw new UnsupportedOperationException();
	}

}
